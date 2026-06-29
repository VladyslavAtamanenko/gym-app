package com.epam.training.service.impl;

import com.epam.training.dao.TrainerDao;
import com.epam.training.dao.TrainingTypeDao;
import com.epam.training.dto.*;
import com.epam.training.exception.TrainerNotFoundException;
import com.epam.training.mapper.ToDTOMapper;
import com.epam.training.mapper.ToEntityMapper;
import com.epam.training.model.Trainer;
import com.epam.training.model.User;
import com.epam.training.service.TrainerService;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class TrainerServiceImpl implements TrainerService {

    private static final Logger log = LoggerFactory.getLogger(TrainerServiceImpl.class);

    private final TrainerDao trainerDao;
    private final TrainingTypeDao specializationDao;
    private final ToEntityMapper<TrainerCreateRequest, Trainer> trainerCreateRequestMapper;
    private final ToDTOMapper<Trainer, TrainerCreateResponse> trainerCreateResponseMapper;
    private final ToDTOMapper<Trainer, TrainerGetResponse> trainerGetResponseMapper;
    private final ToDTOMapper<Trainer, TrainerUpdateResponse> trainerUpdateResponseMapper;
    private final ToDTOMapper<Trainer, TrainerDTO> trainerMapper;
    private final UserUtil userUtil;
    private final PasswordEncoder passwordEncoder;
    private final Counter loginSuccess;
    private final Counter loginFailure;

    @Autowired
    public TrainerServiceImpl(
            TrainerDao trainerDao,
            TrainingTypeDao specializationDao,
            ToEntityMapper<TrainerCreateRequest, Trainer> trainerCreateRequestMapper,
            ToDTOMapper<Trainer, TrainerCreateResponse> trainerCreateResponseMapper,
            ToDTOMapper<Trainer, TrainerGetResponse> trainerGetResponseMapper,
            ToDTOMapper<Trainer, TrainerUpdateResponse> trainerUpdateResponseMapper,
            ToDTOMapper<Trainer, TrainerDTO> trainerMapper,
            UserUtil userUtil,
            PasswordEncoder passwordEncoder,
            MeterRegistry meterRegistry) {
        this.trainerDao = trainerDao;
        this.specializationDao = specializationDao;
        this.trainerCreateRequestMapper = trainerCreateRequestMapper;
        this.trainerCreateResponseMapper = trainerCreateResponseMapper;
        this.trainerGetResponseMapper = trainerGetResponseMapper;
        this.trainerUpdateResponseMapper = trainerUpdateResponseMapper;
        this.trainerMapper = trainerMapper;
        this.userUtil = userUtil;
        this.passwordEncoder = passwordEncoder;
        this.loginSuccess = Counter.builder("gym.login.attempts")
                .tag("role", "trainer").tag("result", "success")
                .description("Successful trainer login attempts")
                .register(meterRegistry);
        this.loginFailure = Counter.builder("gym.login.attempts")
                .tag("role", "trainer").tag("result", "failure")
                .description("Failed trainer login attempts")
                .register(meterRegistry);
    }

    @Counted(value = "gym.trainer.registrations", description = "Total trainer registrations")
    @Override
    public TrainerCreateResponse create(TrainerCreateRequest trainer) {
        if (trainer == null) {
            log.warn("Rejected trainer creation request because request body is null");
            throw new IllegalArgumentException();
        }
        validateCreateRequest(trainer);
        log.debug("Creating trainer from request");
        Trainer created = trainerCreateRequestMapper.toEntity(trainer);
        created.setSpecialization(specializationDao.findByName(trainer.getSpecialization()));
        User user = created.getUser();
        String plainPassword = userUtil.initializeUser(user);
        Trainer saved = trainerDao.save(created);
        log.info("Trainer created successfully. trainerId={}, trainerUsername={}", saved.getId(), saved.getUser().getUsername());
        TrainerCreateResponse response = trainerCreateResponseMapper.toDTO(saved);
        response.setPassword(plainPassword);
        return response;
    }

    @Override
    public Boolean credentialsMatch(LoginRequest credentials) {
        if (credentials == null) {
            log.warn("Rejected login request because request body is null");
            throw new IllegalArgumentException();
        }
        validateLoginRequest(credentials);
        Optional<Trainer> found = trainerDao.findByUsername(credentials.getUsername());
        if (found.isEmpty()) {
            log.warn("Login failed because trainer was not found. trainerUsername={}", credentials.getUsername());
            return false;
        }
        Trainer trainer = found.get();
        User user = trainer.getUser();
        boolean passwordsMatch = passwordEncoder.matches(credentials.getPassword(), user.getPassword());
        if (passwordsMatch) {
            log.info("Successful login. trainerId={}, trainerUsername={}", trainer.getId(), user.getUsername());
            loginSuccess.increment();
        } else {
            log.warn("Login failed because provided password doesn't match current password");
            loginFailure.increment();
        }
        return passwordsMatch;
    }

    @Override
    public Boolean changePassword(ChangeLoginRequest request) {
        if (request == null) {
            log.warn("Rejected change password request because request body is null");
            throw new IllegalArgumentException();
        }
        validateChangePasswordRequest(request);
        Trainer updated = findTrainerOrThrow(request.getUsername());
        boolean passwordsMatch = passwordEncoder.matches(request.getOldPassword(), updated.getUser().getPassword());
        boolean success = false;
        if (passwordsMatch) {
            updated.getUser().setPassword(passwordEncoder.encode(request.getNewPassword()));
            log.info("Password updated successfully. trainerId={}, trainerUsername={}", updated.getId(), updated.getUser().getUsername());
            success = true;
        } else {
            log.warn("Password change failed because provided old password doesn't match current password");
        }
        return success;
    }

    @Override
    public TrainerUpdateResponse update(String username, TrainerUpdateRequest trainer) {
        ValidationUtil.requireNonBlank(username, "username");
        if (trainer == null) {
            log.warn("Rejected trainer update request because request body is null");
            throw new IllegalArgumentException();
        }
        validateUpdateRequest(trainer);
        log.debug("Updating trainer. trainerUsername={}", username);
        Trainer updated = findTrainerOrThrow(username);
        User updatedUser = User.builder()
                .firstName(trainer.getFirstName())
                .lastName(trainer.getLastName())
                .isActive(trainer.getIsActive())
                .build();
        updated.setUser(userUtil.updateUser(updated.getUser(), updatedUser));
        Trainer saved = trainerDao.save(updated);
        log.info("Trainer updated successfully. trainerId={}, trainerUsername={}", saved.getId(), saved.getUser().getUsername());
        return trainerUpdateResponseMapper.toDTO(saved);
    }

    @Override
    public TrainerGetResponse activate(String username) {
        ValidationUtil.requireNonBlank(username, "username");
        log.debug("Activating trainer. trainerUsername={}", username);
        Trainer trainer = findTrainerOrThrow(username);
        rejectIdempotentActiveChange(trainer.getUser().getIsActive(), true);
        trainer.getUser().setIsActive(true);
        Trainer saved = trainerDao.save(trainer);
        log.info("Trainer activated. trainerId={}, trainerUsername={}", saved.getId(), username);
        return trainerGetResponseMapper.toDTO(saved);
    }

    @Override
    public TrainerGetResponse deactivate(String username) {
        ValidationUtil.requireNonBlank(username, "username");
        log.debug("Deactivating trainer. trainerUsername={}", username);
        Trainer trainer = findTrainerOrThrow(username);
        rejectIdempotentActiveChange(trainer.getUser().getIsActive(), false);
        trainer.getUser().setIsActive(false);
        Trainer saved = trainerDao.save(trainer);
        log.info("Trainer deactivated. trainerId={}, trainerUsername={}", saved.getId(), username);
        return trainerGetResponseMapper.toDTO(saved);
    }

    @Override
    public TrainerGetResponse findByUsername(String username) {
        ValidationUtil.requireNonBlank(username, "username");
        Trainer trainer = trainerDao.findByUsername(username).orElseThrow(() -> {
            log.warn("Trainer not found. trainerUsername={}", username);
            return new TrainerNotFoundException(username);
        });
        log.debug("Trainer lookup completed. trainerUsername={}", username);
        return trainerGetResponseMapper.toDTO(trainer);
    }

    @Override
    public Page<TrainerDTO> findNotAssignedOnTrainee(String traineeUsername, Pageable pageable) {
        ValidationUtil.requireNonBlank(traineeUsername, "username");
        Page<TrainerDTO> result = trainerDao.findNotAssignedOnTrainee(traineeUsername, pageable)
                .map(trainerMapper::toDTO);
        log.debug("Trainers not assigned on trainee traineeUsername={} list retrieved. count={}", traineeUsername, result.getNumberOfElements());
        return result;
    }

    @Override
    public List<TrainerGetResponse> findAll() {
        List<TrainerGetResponse> result = trainerDao.findAll()
                .stream()
                .map(trainerGetResponseMapper::toDTO)
                .collect(Collectors.toList());
        log.debug("Trainer list retrieved. count={}", result.size());
        return result;
    }

    private void validateCreateRequest(TrainerCreateRequest trainer) {
        ValidationUtil.requireNonBlank(trainer.getFirstName(), "firstName");
        ValidationUtil.requireNonBlank(trainer.getLastName(), "lastName");
        ValidationUtil.requireNonBlank(trainer.getSpecialization(), "specialization");
    }

    private void validateLoginRequest(LoginRequest credentials) {
        ValidationUtil.requireNonBlank(credentials.getUsername(), "username");
        ValidationUtil.requireNonBlank(credentials.getPassword(), "password");
    }

    private void validateChangePasswordRequest(ChangeLoginRequest request) {
        ValidationUtil.requireNonBlank(request.getUsername(), "username");
        ValidationUtil.requireNonBlank(request.getOldPassword(), "oldPassword");
        ValidationUtil.requireNonBlank(request.getNewPassword(), "newPassword");
    }

    private void validateUpdateRequest(TrainerUpdateRequest trainer) {
        ValidationUtil.requireNonBlank(trainer.getFirstName(), "firstName");
        ValidationUtil.requireNonBlank(trainer.getLastName(), "lastName");
        ValidationUtil.requireNonNull(trainer.getIsActive(), "isActive");
    }

    private Trainer findTrainerOrThrow(String username) {
        return trainerDao.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("Trainer not found. trainerUsername={}", username);
                    return new TrainerNotFoundException(username);
                });
    }

    private void rejectIdempotentActiveChange(Boolean current, boolean requested) {
        if (current != null && current == requested) {
            log.warn("Rejected trainer active state change because requested state already matches current state");
            throw new IllegalStateException("Trainer active state is already " + requested);
        }
    }
}

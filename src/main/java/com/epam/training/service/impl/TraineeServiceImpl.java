package com.epam.training.service.impl;

import com.epam.training.dao.TraineeDao;
import com.epam.training.dao.TrainerDao;
import com.epam.training.dto.*;
import com.epam.training.exception.TraineeNotFoundException;
import com.epam.training.exception.TrainerNotFoundException;
import com.epam.training.mapper.ToDTOMapper;
import com.epam.training.mapper.ToEntityMapper;
import com.epam.training.model.Trainee;
import com.epam.training.model.Trainer;
import com.epam.training.model.User;
import com.epam.training.service.TraineeService;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class TraineeServiceImpl implements TraineeService {

    private static final Logger log = LoggerFactory.getLogger(TraineeServiceImpl.class);

    private final TraineeDao traineeDao;
    private final TrainerDao trainerDao;
    private final ToEntityMapper<TraineeCreateRequest, Trainee> traineeCreateRequestMapper;
    private final ToDTOMapper<Trainee, TraineeCreateResponse> traineeCreateResponseMapper;
    private final ToDTOMapper<Trainee, TraineeUpdateResponse> traineeUpdateResponseMapper;
    private final ToDTOMapper<Trainee, TraineeGetResponse> traineeGetResponseMapper;
    private final ToDTOMapper<Trainer, TrainerDTO> trainerMapper;
    private final UserUtil userUtil;
    private final PasswordEncoder passwordEncoder;
    private final Counter loginSuccess;
    private final Counter loginFailure;

    @Autowired
    public TraineeServiceImpl(
            TraineeDao traineeDao,
            TrainerDao trainerDao,
            ToEntityMapper<TraineeCreateRequest, Trainee> traineeCreateRequestMapper,
            ToDTOMapper<Trainee, TraineeCreateResponse> traineeCreateResponseMapper,
            ToDTOMapper<Trainee, TraineeUpdateResponse> traineeUpdateResponseMapper,
            ToDTOMapper<Trainee, TraineeGetResponse> traineeGetResponseMapper,
            ToDTOMapper<Trainer, TrainerDTO> trainerMapper,
            UserUtil userUtil,
            PasswordEncoder passwordEncoder,
            MeterRegistry meterRegistry) {
        this.traineeDao = traineeDao;
        this.trainerDao = trainerDao;
        this.traineeCreateRequestMapper = traineeCreateRequestMapper;
        this.traineeCreateResponseMapper = traineeCreateResponseMapper;
        this.traineeUpdateResponseMapper = traineeUpdateResponseMapper;
        this.traineeGetResponseMapper = traineeGetResponseMapper;
        this.trainerMapper = trainerMapper;
        this.userUtil = userUtil;
        this.passwordEncoder = passwordEncoder;
        this.loginSuccess = Counter.builder("gym.login.attempts")
                .tag("role", "trainee").tag("result", "success")
                .description("Successful trainee login attempts")
                .register(meterRegistry);
        this.loginFailure = Counter.builder("gym.login.attempts")
                .tag("role", "trainee").tag("result", "failure")
                .description("Failed trainee login attempts")
                .register(meterRegistry);
    }

    @Counted(value = "gym.trainee.registrations", description = "Total trainee registrations")
    @Override
    public TraineeCreateResponse create(TraineeCreateRequest trainee) {
        if (trainee == null) {
            log.warn("Rejected trainee creation request because request body is null");
            throw new IllegalArgumentException();
        }
        validateCreateRequest(trainee);
        log.debug("Creating trainee from request");
        Trainee created = traineeCreateRequestMapper.toEntity(trainee);
        User user = created.getUser();
        String plainPassword = userUtil.initializeUser(user);
        Trainee saved = traineeDao.save(created);
        log.info("Trainee created successfully. traineeUsername={}", saved.getUser().getUsername());
        TraineeCreateResponse response = traineeCreateResponseMapper.toDTO(saved);
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
        Optional<Trainee> found = traineeDao.findByUsername(credentials.getUsername());
        if (found.isEmpty()) {
            log.warn("Login failed because trainee was not found. traineeUsername={}", credentials.getUsername());
            return false;
        }
        Trainee trainee = found.get();
        User user = trainee.getUser();
        boolean passwordsMatch = passwordEncoder.matches(credentials.getPassword(), user.getPassword());
        if (passwordsMatch) {
            log.info("Successful login. traineeId={}, traineeUsername={}", trainee.getId(), user.getUsername());
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
        Trainee updated = traineeDao.findByUsername(request.getUsername())
                .orElseThrow(() -> {
                    log.warn("Password change failed because trainee was not found. traineeUsername={}", request.getUsername());
                    return new TraineeNotFoundException(request.getUsername());
                });
        boolean passwordsMatch = passwordEncoder.matches(request.getOldPassword(), updated.getUser().getPassword());
        boolean success = false;
        if (passwordsMatch) {
            updated.getUser().setPassword(passwordEncoder.encode(request.getNewPassword()));
            log.info("Password updated successfully. traineeId={}, traineeUsername={}", updated.getId(), updated.getUser().getUsername());
            success = true;
        } else {
            log.warn("Password change failed because provided old password doesn't match current password");
        }
        return success;
    }

    @Override
    public TraineeUpdateResponse update(String username, TraineeUpdateRequest trainee) {
        ValidationUtil.requireNonBlank(username, "username");
        if (trainee == null) {
            log.warn("Rejected trainee update request because request body is null");
            throw new IllegalArgumentException();
        }
        validateUpdateRequest(trainee);
        log.debug("Updating trainee. traineeUsername={}", username);
        Trainee updated = traineeDao.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("Trainee update failed because trainee was not found. traineeUsername={}", username);
                    return new TraineeNotFoundException(username);
                });
        updated.setAddress(trainee.getAddress());
        updated.setDateOfBirth(trainee.getDateOfBirth());
        User updatedUser = User.builder()
                .firstName(trainee.getFirstName())
                .lastName(trainee.getLastName())
                .isActive(trainee.getIsActive())
                .build();
        updated.setUser(userUtil.updateUser(updated.getUser(), updatedUser));
        Trainee saved = traineeDao.save(updated);
        log.info("Trainee updated successfully. traineeId={}, traineeUsername={}", saved.getId(), saved.getUser().getUsername());
        return traineeUpdateResponseMapper.toDTO(saved);
    }

    @Override
    public TraineeGetResponse activate(String username) {
        ValidationUtil.requireNonBlank(username, "username");
        log.debug("Activating trainee. traineeUsername={}", username);
        Trainee trainee = findTraineeOrThrow(username);
        rejectIdempotentActiveChange(trainee.getUser().getIsActive(), true);
        trainee.getUser().setIsActive(true);
        Trainee saved = traineeDao.save(trainee);
        log.info("Trainee activated. traineeId={}, traineeUsername={}", saved.getId(), username);
        return traineeGetResponseMapper.toDTO(saved);
    }

    @Override
    public TraineeGetResponse deactivate(String username) {
        ValidationUtil.requireNonBlank(username, "username");
        log.debug("Deactivating trainee. traineeUsername={}", username);
        Trainee trainee = findTraineeOrThrow(username);
        rejectIdempotentActiveChange(trainee.getUser().getIsActive(), false);
        trainee.getUser().setIsActive(false);
        Trainee saved = traineeDao.save(trainee);
        log.info("Trainee deactivated. traineeId={}, traineeUsername={}", saved.getId(), username);
        return traineeGetResponseMapper.toDTO(saved);
    }

    @Override
    public List<TrainerDTO> updateTrainersList(String username, TraineeUpdateTrainersRequest request) {
        ValidationUtil.requireNonBlank(username, "username");
        if (request == null) {
            log.warn("Rejected trainers list update because request body is null");
            throw new IllegalArgumentException();
        }
        ValidationUtil.requireNotEmpty(request.getTrainers(), "trainers");
        Trainee trainee = traineeDao.findByUsername(username).orElseThrow(() -> {
            log.warn("Trainers list update failed because trainee was not found. traineeUsername={}", username);
            return new TraineeNotFoundException(username);
        });
        if (trainee.getTrainers() == null) {
            trainee.setTrainers(new ArrayList<>());
        }
        trainee.getTrainers().clear();
        List<Trainer> trainers = new ArrayList<>();
        for (String trainerUsername : request.getTrainers()) {
            Trainer trainer = trainerDao.findByUsername(trainerUsername)
                    .orElseThrow(() -> {
                        log.warn("Trainers list update failed because trainer was not found. trainerUsername={}", trainerUsername);
                        return new TrainerNotFoundException(trainerUsername);
                    });
            trainers.add(trainer);
        }
        trainee.getTrainers().addAll(trainers);
        log.info("Trainees trainer list updated successfully. traineeId={}, traineeUsername={}", trainee.getId(), trainee.getUser().getUsername());
        return trainee.getTrainers().stream().map(trainerMapper::toDTO).toList();
    }

    @Counted(value = "gym.trainee.deletions", description = "Total trainee deletions")
    @Override
    public void delete(String username) {
        ValidationUtil.requireNonBlank(username, "username");
        log.debug("Deleting trainee. traineeUsername={}", username);
        findTraineeOrThrow(username);
        traineeDao.delete(username);
    }

    @Override
    public TraineeGetResponse findByUsername(String username) {
        ValidationUtil.requireNonBlank(username, "username");
        Trainee trainee = traineeDao.findByUsername(username).orElseThrow(() -> {
            log.warn("Trainee not found. traineeUsername={}", username);
            return new TraineeNotFoundException(username);
        });
        log.debug("Trainee lookup completed. traineeUsername={}", username);
        return traineeGetResponseMapper.toDTO(trainee);
    }

    @Override
    public List<TraineeGetResponse> findAll() {
        List<TraineeGetResponse> result = traineeDao.findAll().stream()
                .map(traineeGetResponseMapper::toDTO)
                .collect(Collectors.toList());
        log.debug("Trainee list retrieved. count={}", result.size());
        return result;
    }

    private void validateCreateRequest(TraineeCreateRequest trainee) {
        ValidationUtil.requireNonBlank(trainee.getFirstName(), "firstName");
        ValidationUtil.requireNonBlank(trainee.getLastName(), "lastName");
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

    private void validateUpdateRequest(TraineeUpdateRequest trainee) {
        ValidationUtil.requireNonBlank(trainee.getFirstName(), "firstName");
        ValidationUtil.requireNonBlank(trainee.getLastName(), "lastName");
        ValidationUtil.requireDate(trainee.getDateOfBirth(), "dateOfBirth");
        ValidationUtil.requireNonBlank(trainee.getAddress(), "address");
        ValidationUtil.requireNonNull(trainee.getIsActive(), "isActive");
    }

    private void rejectIdempotentActiveChange(Boolean current, Boolean requested) {
        if (current != null && current.equals(requested)) {
            log.warn("Rejected trainee active state update because requested state already matches current state");
            throw new IllegalStateException("Trainee active state is already " + requested);
        }
    }

    private Trainee findTraineeOrThrow(String username) {
        return traineeDao.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("Trainee not found. traineeUsername={}", username);
                    return new TraineeNotFoundException(username);
                });
    }
}

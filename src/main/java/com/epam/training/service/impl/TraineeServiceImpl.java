package com.epam.training.service.impl;

import com.epam.training.dao.TraineeDao;
import com.epam.training.dao.TrainerDao;
import com.epam.training.dto.*;
import com.epam.training.dto.TraineeCreateResponse;
import com.epam.training.mapper.ToDTOMapper;
import com.epam.training.mapper.ToEntityMapper;
import com.epam.training.model.Trainee;
import com.epam.training.model.Trainer;
import com.epam.training.model.User;
import com.epam.training.service.TraineeService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class TraineeServiceImpl implements TraineeService {

    private static final Log LOGGER = LogFactory.getLog(TraineeServiceImpl.class);


    private ToEntityMapper<TraineeCreateRequest, Trainee> traineeCreateRequestMapper;
    private ToDTOMapper<Trainee, TraineeCreateResponse> traineeCreateResponseMapper;
    private ToDTOMapper<Trainee, TraineeUpdateResponse> traineeUpdateResponseMapper;
    private ToDTOMapper<Trainee, TraineeGetResponse> traineeGetResponseMapper;
    private ToDTOMapper<Trainer, TrainerDTO> trainerMapper;
    private UserUtil userUtil;

    @Autowired
    private TraineeDao traineeDao;
    @Autowired
    private TrainerDao trainerDao;


    @Override
    public TraineeCreateResponse create(TraineeCreateRequest trainee) {
        if (trainee == null) {
            LOGGER.warn("Rejected trainee creation request because request body is null");
            throw new IllegalArgumentException();
        }
        validateCreateRequest(trainee);
        LOGGER.debug("Creating trainee from request");
        Trainee created = traineeCreateRequestMapper.toEntity(trainee);
        User user = created.getUser();
        userUtil.initializeUser(user);
        Trainee saved = traineeDao.save(created);
        LOGGER.info("Trainee created successfully. traineeUsername=" + saved.getUser().getUsername());
        return traineeCreateResponseMapper.toDTO(saved);
    }

    @Override
    public Boolean credentialsMatch(LoginRequest credentials) {
        if (credentials == null) {
            LOGGER.warn("Rejected login request because request body is null");
            throw new IllegalArgumentException();
        }
        validateLoginRequest(credentials);
        Trainee trainee = traineeDao.findByUsername(credentials.getUsername())
                .orElseThrow(() -> {
                    LOGGER.warn("Login failed because trainee was not found. traineeUsername=" + credentials.getUsername());
                    return new NoSuchElementException();
                });

        User user = trainee.getUser();
        if (!Boolean.TRUE.equals(user.getIsActive())) {
            LOGGER.warn("Login failed because trainee is inactive. traineeUsername=" + user.getUsername());
            return false;
        }
        boolean passwordsMatch = user.getPassword().equals(credentials.getPassword());
        if (passwordsMatch) {
            LOGGER.info("Successful login. traineeId=" + trainee.getId() + "traineeUsername=" + user.getUsername());
        } else {
            LOGGER.warn("Login failed because provided password doesn't match current password");
        }
        return passwordsMatch;
    }

    @Override
    public Boolean changePassword(ChangeLoginRequest request) {
        if (request == null) {
            LOGGER.warn("Rejected change password request because request body is null");
            throw new IllegalArgumentException();
        }
        validateChangePasswordRequest(request);
        Trainee updated = traineeDao.findByUsername(request.getUsername())
                .orElseThrow(() -> {
                    LOGGER.warn("Password change failed because trainee was not found. traineeUsername=" + request.getUsername());
                    return new NoSuchElementException();
                });


        String currentPassword = updated.getUser().getPassword();
        boolean passwordsMatch = currentPassword.equals(request.getOldPassword());
        boolean success = false;
        if(passwordsMatch){
            updated.getUser().setPassword(request.getNewPassword());
            LOGGER.info("Password updated successfully. traineeId=" + updated.getId() + "traineeUsername=" + updated.getUser().getUsername());
            success = true;
        } else {
            LOGGER.warn("Password change failed because provided old password doesn't match current password");
        }
        return success;
    }

    @Override
    public TraineeUpdateResponse update(TraineeUpdateRequest trainee) {
        if (trainee == null) {
            LOGGER.warn("Rejected trainee update request because request body is null");
            throw new IllegalArgumentException();
        }
        validateUpdateRequest(trainee);
        LOGGER.debug("Updating trainee. traineeUsername=" + trainee.getUsername());
        Trainee updated = traineeDao.findByUsername(trainee.getUsername())
                .orElseThrow(() -> {
                    LOGGER.warn("Trainee update failed because trainee was not found. traineeUsername=" + trainee.getUsername());
                    return new NoSuchElementException();
                });
        updated.setAddress(trainee.getAddress());
        updated.setDateOfBirth(trainee.getDateOfBirth());
        User updatedUser = User.builder()
                .firstName(trainee.getFirstName())
                .lastName(trainee.getLastName())
                .isActive(updated.getUser().getIsActive())
                .build();
        updated.setUser(userUtil.updateUser(updated.getUser(), updatedUser));
        Trainee saved = traineeDao.save(updated);
        LOGGER.info("Trainee updated successfully. traineeId=" + saved.getId() + "traineeUsername=" + saved.getUser().getUsername());
        return traineeUpdateResponseMapper.toDTO(saved);
    }

    @Override
    public TraineeGetResponse activate(String username) {
        ValidationUtil.requireNonBlank(username, "username");
        LOGGER.debug("Activating trainee. traineeUsername=" + username);
        Trainee trainee = findTraineeOrThrow(username);
        rejectIdempotentActiveChange(trainee.getUser().getIsActive(), true);
        trainee.getUser().setIsActive(true);
        Trainee saved = traineeDao.save(trainee);
        LOGGER.info("Trainee activated. traineeId=" + saved.getId() + ", traineeUsername=" + username);
        return traineeGetResponseMapper.toDTO(saved);
    }

    @Override
    public TraineeGetResponse deactivate(String username) {
        ValidationUtil.requireNonBlank(username, "username");
        LOGGER.debug("Deactivating trainee. traineeUsername=" + username);
        Trainee trainee = findTraineeOrThrow(username);
        rejectIdempotentActiveChange(trainee.getUser().getIsActive(), false);
        trainee.getUser().setIsActive(false);
        Trainee saved = traineeDao.save(trainee);
        LOGGER.info("Trainee deactivated. traineeId=" + saved.getId() + ", traineeUsername=" + username);
        return traineeGetResponseMapper.toDTO(saved);
    }

    @Override
    public List<TrainerDTO> updateTrainersList(TraineeUpdateTrainersRequest request) {
        if (request == null) {
            LOGGER.warn("Rejected trainers list update because request body is null");
            throw new IllegalArgumentException();
        }
        ValidationUtil.requireNonBlank(request.getUsername(), "username");
        ValidationUtil.requireNotEmpty(request.getTrainers(), "trainers");
        Trainee trainee = traineeDao.findByUsername(request.getUsername()).orElseThrow(() -> {
            LOGGER.warn("Trainers list update failed because trainee was not found. traineeUsername=" + request.getUsername());
            return new NoSuchElementException();
        });

        if (trainee.getTrainers() == null) {
            trainee.setTrainers(new ArrayList<>());
        }
        trainee.getTrainers().clear();
        List<Trainer> trainers = new ArrayList<>();

        request.getTrainers().stream()
                .map(t -> trainerDao.findByUsername(t))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(trainers::add);

        trainee.getTrainers().addAll(trainers);

        LOGGER.info("Trainees trainer list updated successfully. traineeId=" + trainee.getId() +
                "traineeUsername=" + trainee.getUser().getUsername());

        return trainee.getTrainers().stream().map(trainerMapper::toDTO).toList();
    }

    @Override
    public void delete(String username) {
        ValidationUtil.requireNonBlank(username, "username");
        LOGGER.debug("Deleting trainee. traineeUsername=" + username);
        traineeDao.delete(username);
    }

    @Override
    public Optional<TraineeGetResponse> findByUsername(String username) {
        ValidationUtil.requireNonBlank(username, "username");
        Optional<TraineeGetResponse> result = traineeDao.findByUsername(username).map(traineeGetResponseMapper::toDTO);
        LOGGER.debug("Trainee lookup completed. traineeUsername" + username + ", found=" + result.isPresent());
        return result;
    }

    @Override
    public List<TraineeGetResponse> findAll() {
        List<TraineeGetResponse> result = traineeDao.findAll().stream()
                .map(traineeGetResponseMapper::toDTO)
                .collect(Collectors.toList());
        LOGGER.debug("Trainee list retrieved. count=" + result.size());
        return result;
    }

    private void validateCreateRequest(TraineeCreateRequest trainee) {
        ValidationUtil.requireNonBlank(trainee.getFirstName(), "firstName");
        ValidationUtil.requireNonBlank(trainee.getLastName(), "lastName");
        ValidationUtil.requireDate(trainee.getDateOfBirth(), "dateOfBirth");
        ValidationUtil.requireNonBlank(trainee.getAddress(), "address");
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
        ValidationUtil.requireNonBlank(trainee.getUsername(), "username");
        ValidationUtil.requireNonBlank(trainee.getFirstName(), "firstName");
        ValidationUtil.requireNonBlank(trainee.getLastName(), "lastName");
        ValidationUtil.requireDate(trainee.getDateOfBirth(), "dateOfBirth");
        ValidationUtil.requireNonBlank(trainee.getAddress(), "address");
    }

    private void rejectIdempotentActiveChange(Boolean current, Boolean requested) {
        if (current != null && current.equals(requested)) {
            LOGGER.warn("Rejected trainee active state update because requested state already matches current state");
            throw new IllegalStateException("Trainee active state is already " + requested);
        }
    }

    private Trainee findTraineeOrThrow(String username) {
        return traineeDao.findByUsername(username)
                .orElseThrow(() -> {
                    LOGGER.warn("Trainee not found. traineeUsername=" + username);
                    return new NoSuchElementException();
                });
    }

    @Autowired
    public void setUserUtil(UserUtil userUtil) {
        this.userUtil = userUtil;
    }

    @Autowired
    public void setTraineeCreateRequestMapper(ToEntityMapper<TraineeCreateRequest, Trainee> traineeCreateRequestMapper) {
        this.traineeCreateRequestMapper = traineeCreateRequestMapper;
    }

    @Autowired
    public void setTraineeCreateResponseMapper(ToDTOMapper<Trainee, TraineeCreateResponse> traineeCreateResponseMapper) {
        this.traineeCreateResponseMapper = traineeCreateResponseMapper;
    }

    @Autowired
    public void setTraineeUpdateResponseMapper(ToDTOMapper<Trainee, TraineeUpdateResponse> traineeUpdateResponseMapper) {
        this.traineeUpdateResponseMapper = traineeUpdateResponseMapper;
    }

    @Autowired
    public void setTraineeGetResponseMapper(ToDTOMapper<Trainee, TraineeGetResponse> traineeGetResponseMapper) {
        this.traineeGetResponseMapper = traineeGetResponseMapper;
    }

    @Autowired
    public void setTrainerMapper(ToDTOMapper<Trainer, TrainerDTO> trainerMapper) {
        this.trainerMapper = trainerMapper;
    }
}

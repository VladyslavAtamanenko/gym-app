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

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
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
        Trainee trainee = traineeDao.findByUsername(credentials.getUsername())
                .orElseThrow(() -> {
                    LOGGER.warn("Login failed because trainee was not found. traineeUsername=" + credentials.getUsername());
                    return new NoSuchElementException();
                });

        String password = trainee.getUser().getPassword();
        boolean passwordsMatch = password.equals(credentials.getPassword());
        boolean success = false;
        if(passwordsMatch){
            LOGGER.info("Successful login. traineeId=" + trainee.getId() + "traineeUsername=" + trainee.getUser().getUsername());
            success = true;
        } else {
            LOGGER.warn("Login failed because provided password doesn't match current password");
        }
        return success;
    }

    @Override
    public Boolean changePassword(ChangeLoginRequest request) {
        if (request == null) {
            LOGGER.warn("Rejected change password request because request body is null");
            throw new IllegalArgumentException();
        }
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
                .isActive(trainee.getIsActive())
                .build();
        updated.setUser(userUtil.updateUser(updated.getUser(), updatedUser));
        Trainee saved = traineeDao.save(updated);
        LOGGER.info("Trainee updated successfully. traineeId=" + saved.getId() + "traineeUsername=" + saved.getUser().getUsername());
        return traineeUpdateResponseMapper.toDTO(saved);
    }

    @Override
    public List<TrainerDTO> updateTrainersList(TraineeUpdateTrainersRequest request) {
        Trainee trainee = traineeDao.findByUsername(request.getUsername()).orElseThrow(() -> {
            LOGGER.warn("Trainers list update failed because trainee was not found. traineeUsername=" + request.getUsername());
            return new NoSuchElementException();
        });

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
        LOGGER.debug("Deleting trainee. traineeUsername=" + username);
        traineeDao.delete(username);
    }

    @Override
    public Optional<TraineeGetResponse> findByUsername(String username) {
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

    public void setTrainerMapper(ToDTOMapper<Trainer, TrainerDTO> trainerMapper) {
        this.trainerMapper = trainerMapper;
    }
}

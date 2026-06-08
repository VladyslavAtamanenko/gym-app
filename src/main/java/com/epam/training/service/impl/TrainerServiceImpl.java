package com.epam.training.service.impl;

import com.epam.training.dao.TrainerDao;
import com.epam.training.dao.TrainingTypeDao;
import com.epam.training.dto.*;
import com.epam.training.mapper.Mapper;
import com.epam.training.mapper.ToDTOMapper;
import com.epam.training.mapper.ToEntityMapper;
import com.epam.training.model.Trainee;
import com.epam.training.model.Trainer;
import com.epam.training.model.TrainingType;
import com.epam.training.model.User;
import com.epam.training.service.TrainerService;
import com.epam.training.util.PasswordGenerator;
import com.epam.training.util.UsernameGenerator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TrainerServiceImpl implements TrainerService {

    private static final Log LOGGER = LogFactory.getLog(TrainerServiceImpl.class);

    private ToEntityMapper<TrainerCreateRequest, Trainer> trainerCreateRequestMapper;
    private ToDTOMapper<Trainer, TrainerCreateResponse> trainerCreateResponseMapper;
    private ToDTOMapper<Trainer, TrainerGetResponse> trainerGetResponseMapper;
    private ToDTOMapper<Trainer, TrainerUpdateResponse> trainerUpdateResponseMapper;
    private ToDTOMapper<Trainer, TrainerDTO> trainerMapper;
    private UserUtil userUtil;

    @Autowired
    private TrainerDao trainerDao;
    @Autowired
    private TrainingTypeDao specializationDao;

    @Autowired
    private UsernameGenerator usernameGenerator;

    @Autowired
    private PasswordGenerator passwordGenerator;

    @Override
    public TrainerCreateResponse create(TrainerCreateRequest trainer) {
        if (trainer == null) {
            LOGGER.warn("Rejected trainer creation request because request body is null");
            throw new IllegalArgumentException();
        }
        LOGGER.debug("Creating trainer from request");
        Trainer created = trainerCreateRequestMapper.toEntity(trainer);
        User user = created.getUser();
        userUtil.initializeUser(user);
        Trainer saved = trainerDao.save(created);
        LOGGER.info("Trainer created successfully. trainerId=" + saved.getId() + "trainerUsername=" + saved.getUser().getUsername());
        return trainerCreateResponseMapper.toDTO(saved);
    }

    @Override
    public Boolean credentialsMatch(LoginRequest credentials) {
        if (credentials == null) {
            LOGGER.warn("Rejected login request because request body is null");
            throw new IllegalArgumentException();
        }
        Trainer trainer = trainerDao.findByUsername(credentials.getUsername())
                .orElseThrow(() -> {
                    LOGGER.warn("Login failed because trainer was not found. trainerUsername=" + credentials.getUsername());
                    return new NoSuchElementException();
                });

        String password = trainer.getUser().getPassword();
        boolean passwordsMatch = password.equals(credentials.getPassword());
        boolean success = false;
        if(passwordsMatch){
            LOGGER.info("Successful login. trainerId=" + trainer.getId() + "trainerUsername=" + trainer.getUser().getUsername());
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
        Trainer updated = trainerDao.findByUsername(request.getUsername())
                .orElseThrow(() -> {
                    LOGGER.warn("Password change failed because trainer was not found. trainerUsername=" + request.getUsername());
                    return new NoSuchElementException();
                });


        String currentPassword = updated.getUser().getPassword();
        boolean passwordsMatch = currentPassword.equals(request.getOldPassword());
        boolean success = false;
        if(passwordsMatch){
            updated.getUser().setPassword(request.getNewPassword());
            LOGGER.info("Password updated successfully. trainerId=" + updated.getId() + "trainerUsername=" + updated.getUser().getUsername());
            success = true;
        } else {
            LOGGER.warn("Password change failed because provided old password doesn't match current password");
        }
        return success;
    }

    @Override
    public TrainerUpdateResponse update(TrainerUpdateRequest trainer) {
        if (trainer == null) {
            LOGGER.warn("Rejected trainer update request because request body is null");
            throw new IllegalArgumentException();
        }
        LOGGER.debug("Updating trainer. trainerUsername=" + trainer.getUsername());
        Trainer updated = trainerDao.findByUsername(trainer.getUsername())
                .orElseThrow(() -> {
                    LOGGER.warn("Trainer update failed because trainer was not found. trainerUsername=" + trainer.getUsername());
                    return new NoSuchElementException();
                });

        TrainingType specialization = specializationDao.findByName(trainer.getSpecialization());
        updated.setSpecialization(specialization);
        User updatedUser = User.builder()
                .firstName(trainer.getFirstName())
                .lastName(trainer.getLastName())
                .isActive(trainer.getIsActive())
                .build();
        updated.setUser(userUtil.updateUser(updated.getUser(), updatedUser));
        Trainer saved = trainerDao.save(updated);
        LOGGER.info("Trainer updated successfully. trainerId=" + saved.getId() + "trainerUsername=" + saved.getUser().getUsername());
        return trainerUpdateResponseMapper.toDTO(saved);
    }

    @Override
    public Optional<TrainerGetResponse> findByUsername(String username) {
        Optional<TrainerGetResponse> result = trainerDao.findByUsername(username).map(trainerGetResponseMapper::toDTO);
        LOGGER.debug("Trainer lookup completed. trainerUsername=" + username + ", found=" + result.isPresent());
        return result;
    }

    @Override
    public List<TrainerDTO> findNotAssignedOnTrainee(String traineeUsername) {
        List<TrainerDTO> result = trainerDao.findNotAssignedOnTrainee(traineeUsername)
                .stream()
                .map(trainerMapper::toDTO)
                .collect(Collectors.toList());
        LOGGER.debug("Trainers not assigned on trainee traineeUsername=" + traineeUsername + " list retrieved. count=" + result.size());
        return result;
    }

    @Override
    public List<TrainerGetResponse> findAll() {
        List<TrainerGetResponse> result = trainerDao.findAll()
                .stream()
                .map(trainerGetResponseMapper::toDTO)
                .collect(Collectors.toList());
        LOGGER.debug("Trainer list retrieved. count=" + result.size());
        return result;
    }

    @Autowired
    public void setUserUtil(UserUtil userUtil) {
        this.userUtil = userUtil;
    }

    @Autowired
    public void setTrainerCreateRequestMapper(ToEntityMapper<TrainerCreateRequest, Trainer> trainerCreateRequestMapper) {
        this.trainerCreateRequestMapper = trainerCreateRequestMapper;
    }

    @Autowired
    public void setTrainerCreateResponseMapper(ToDTOMapper<Trainer, TrainerCreateResponse> trainerCreateResponseMapper) {
        this.trainerCreateResponseMapper = trainerCreateResponseMapper;
    }

    @Autowired
    public void setTrainerGetResponseMapper(ToDTOMapper<Trainer, TrainerGetResponse> trainerGetResponseMapper) {
        this.trainerGetResponseMapper = trainerGetResponseMapper;
    }

    @Autowired
    public void setTrainerUpdateResponseMapper(ToDTOMapper<Trainer, TrainerUpdateResponse> trainerUpdateResponseMapper) {
        this.trainerUpdateResponseMapper = trainerUpdateResponseMapper;
    }

    @Autowired
    public void setTrainerMapper(ToDTOMapper<Trainer, TrainerDTO> trainerMapper) {
        this.trainerMapper = trainerMapper;
    }
}

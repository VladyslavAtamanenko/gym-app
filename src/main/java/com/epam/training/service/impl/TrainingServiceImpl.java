package com.epam.training.service.impl;

import com.epam.training.dao.TraineeDao;
import com.epam.training.dao.TrainerDao;
import com.epam.training.dao.TrainingDao;
import com.epam.training.dto.*;
import com.epam.training.exception.TraineeNotFoundException;
import com.epam.training.exception.TrainerNotFoundException;
import com.epam.training.mapper.ToDTOMapper;
import com.epam.training.mapper.ToEntityMapper;
import com.epam.training.model.Trainee;
import com.epam.training.model.Trainer;
import com.epam.training.model.Training;
import com.epam.training.service.TrainingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Objects;

@Service
@Transactional
public class TrainingServiceImpl implements TrainingService {

    private static final Logger log = LoggerFactory.getLogger(TrainingServiceImpl.class);

    private final TrainingDao trainingDao;
    private final TraineeDao traineeDao;
    private final TrainerDao trainerDao;
    private final ToEntityMapper<TrainingCreateRequest, Training> trainingCreateRequestMapper;
    private final ToDTOMapper<Training, GetTrainingsByTraineeResponse> byTraineeResponseMapper;
    private final ToDTOMapper<Training, GetTrainingsByTrainerResponse> byTrainerResponseMapper;

    @Autowired
    public TrainingServiceImpl(
            TrainingDao trainingDao,
            TraineeDao traineeDao,
            TrainerDao trainerDao,
            ToEntityMapper<TrainingCreateRequest, Training> trainingCreateRequestMapper,
            ToDTOMapper<Training, GetTrainingsByTraineeResponse> byTraineeResponseMapper,
            ToDTOMapper<Training, GetTrainingsByTrainerResponse> byTrainerResponseMapper) {
        this.trainingDao = trainingDao;
        this.traineeDao = traineeDao;
        this.trainerDao = trainerDao;
        this.trainingCreateRequestMapper = trainingCreateRequestMapper;
        this.byTraineeResponseMapper = byTraineeResponseMapper;
        this.byTrainerResponseMapper = byTrainerResponseMapper;
    }

    @Override
    public Boolean create(TrainingCreateRequest training) {
        if (training == null) {
            log.warn("Rejected training creation request because request body is null");
            throw new IllegalArgumentException();
        }
        validateCreateRequest(training);

        String traineeUsername = training.getTrainee();
        String trainerUsername = training.getTrainer();
        log.debug("Creating training. traineeUsername={}, trainerUsername={}", traineeUsername, trainerUsername);

        Trainer trainer = trainerDao.findByUsername(trainerUsername)
                .orElseThrow(() -> {
                    log.warn("Training creation failed because trainer was not found. trainerUsername={}", trainerUsername);
                    return new TrainerNotFoundException(trainerUsername);
                });

        if (!Objects.equals(trainer.getSpecialization().getName(), training.getType())) {
            log.warn("Training creation failed because trainer specialization doesn't match training type. trainerSpecialization={}, trainingType={}",
                    trainer.getSpecialization().getName(), training.getType());
            throw new IllegalArgumentException();
        }

        Trainee trainee = traineeDao.findByUsername(traineeUsername)
                .orElseThrow(() -> {
                    log.warn("Training creation failed because trainee was not found. traineeUsername={}", traineeUsername);
                    return new TraineeNotFoundException(traineeUsername);
                });

        Training created = trainingCreateRequestMapper.toEntity(training);
        created.setTrainee(trainee);
        created.setTrainer(trainer);
        created.setType(trainer.getSpecialization());

        Training saved = trainingDao.save(created);
        log.info("Training created successfully. trainingId={}, traineeUsername={}, trainerUsername={}", saved.getId(), traineeUsername, trainerUsername);
        return true;
    }

    @Override
    public Page<GetTrainingsByTraineeResponse> findByTrainee(String username, LocalDate from, LocalDate to,
                                                             String trainerName, String trainingType,
                                                             Pageable pageable) {
        ValidationUtil.requireNonBlank(username, "username");
        Page<GetTrainingsByTraineeResponse> result = trainingDao
                .findByTrainee(username, trainingType, from, to, trainerName, pageable)
                .map(byTraineeResponseMapper::toDTO);
        log.debug("Trainee training list retrieved. traineeUsername={}, count={}", username, result.getNumberOfElements());
        return result;
    }

    @Override
    public Page<GetTrainingsByTrainerResponse> findByTrainer(String username, LocalDate from, LocalDate to,
                                                             String traineeName, Pageable pageable) {
        ValidationUtil.requireNonBlank(username, "username");
        Page<GetTrainingsByTrainerResponse> result = trainingDao
                .findByTrainer(username, from, to, traineeName, pageable)
                .map(byTrainerResponseMapper::toDTO);
        log.debug("Trainer training list retrieved. trainerUsername={}, count={}", username, result.getNumberOfElements());
        return result;
    }

    private void validateCreateRequest(TrainingCreateRequest training) {
        ValidationUtil.requireNonBlank(training.getTrainee(), "trainee");
        ValidationUtil.requireNonBlank(training.getTrainer(), "trainer");
        ValidationUtil.requireNonBlank(training.getName(), "name");
        ValidationUtil.requireNonBlank(training.getType(), "type");
        ValidationUtil.requireDate(training.getDate(), "date");
        ValidationUtil.requirePositive(training.getDuration(), "duration");
    }
}

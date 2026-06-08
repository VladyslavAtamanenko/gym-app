package com.epam.training.service.impl;

import com.epam.training.dao.TraineeDao;
import com.epam.training.dao.TrainerDao;
import com.epam.training.dao.TrainingDao;
import com.epam.training.dto.*;
import com.epam.training.mapper.ToDTOMapper;
import com.epam.training.mapper.ToEntityMapper;
import com.epam.training.model.Trainee;
import com.epam.training.model.Trainer;
import com.epam.training.model.Training;
import com.epam.training.service.TrainingService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class TrainingServiceImpl implements TrainingService {

    private static final Log LOGGER = LogFactory.getLog(TrainingServiceImpl.class);

    ToEntityMapper<TrainingCreateRequest, Training> trainingCreateRequestMapper;

    ToDTOMapper<Training, GetTrainingsByTraineeResponse> byTraineeResponseMapper;
    ToDTOMapper<Training, GetTrainingsByTrainerResponse> byTrainerResponseMapper;

    @Autowired
    private TrainingDao trainingDao;

    @Autowired
    private TraineeDao traineeDao;

    @Autowired
    private TrainerDao trainerDao;

    @Override
    public Boolean create(TrainingCreateRequest training) {
        if (training == null) {
            LOGGER.warn("Rejected training creation request because request body is null");
            throw new IllegalArgumentException();
        }

        String traineeUsername = training.getTrainee();
        String trainerUsername = training.getTrainer();
        LOGGER.debug("Creating training. traineeUsername=" + traineeUsername + ", trainerUsername=" + trainerUsername);

        Trainer trainer = trainerDao
                .findByUsername(trainerUsername)
                .orElseThrow(() -> {
                    LOGGER.warn("Training creation failed because trainer was not found. trainerUsername=" + trainerUsername);
                    return new NoSuchElementException();
                });

        if (!Objects.equals(trainer.getSpecialization().getName(), training.getType())){
            LOGGER.warn("Training creation failed because trainer specialization doesn't match training type. " +
                    "trainerSpecialization=" + trainer.getSpecialization().getName() + " trainingType=" + training.getType());
            throw new IllegalArgumentException();
        }

        Trainee trainee = traineeDao
                .findByUsername(traineeUsername)
                .orElseThrow(() -> {
                    LOGGER.warn("Training creation failed because trainee was not found. traineeUsername=" + traineeUsername);
                    return new NoSuchElementException();
                });

        Training created = trainingCreateRequestMapper.toEntity(training);

        created.setTrainee(trainee);
        created.setTrainer(trainer);

        Training saved = trainingDao.save(created);
        LOGGER.info("Training created successfully. trainingId=" + saved.getId()
                + ", traineeUsername=" + traineeUsername + ", trainerId=" + trainerUsername);
        return true;
    }


    @Override
    public List<GetTrainingsByTraineeResponse> findByTrainee(GetTrainingsByTraineeRequest request) {
        List<GetTrainingsByTraineeResponse> result = trainingDao.findByTrainee(request.getUsername(),
                        request.getTrainingType(),
                        request.getFrom(),
                        request.getTo(),
                        request.getTrainer())
                .stream()
                .map(byTraineeResponseMapper::toDTO)
                .collect(Collectors.toList());
        LOGGER.debug("Training list according to request traineeUsername=" + request.getUsername() +
                "trainingType=" +  request.getTrainingType() + " dateFrom=" + request.getFrom() +
                " dateTo=" + request.getTo() + "trainerUsername=" + request.getTrainer() + " retrieved. count=" + result.size());
        return result;
    }

    @Override
    public List<GetTrainingsByTrainerResponse> findByTrainer(GetTrainingsByTrainerRequest request) {
        List<GetTrainingsByTrainerResponse> result = trainingDao.findByTrainer(request.getUsername(),
                        request.getFrom(),
                        request.getTo(),
                        request.getTrainee())
                .stream()
                .map(byTrainerResponseMapper::toDTO)
                .collect(Collectors.toList());
        LOGGER.debug("Training list according to request trainerUsername=" + request.getUsername() +
                " dateFrom=" + request.getFrom() +
                " dateTo=" + request.getTo() + "trainerUsername=" + request.getTrainee() + " retrieved. count=" + result.size());
        return result;
    }

    @Autowired
    public void setTrainingCreateRequestMapper(ToEntityMapper<TrainingCreateRequest, Training> trainingCreateRequestMapper) {
        this.trainingCreateRequestMapper = trainingCreateRequestMapper;
    }

    @Autowired
    public void setByTraineeResponseMapper(ToDTOMapper<Training, GetTrainingsByTraineeResponse> byTraineeResponseMapper) {
        this.byTraineeResponseMapper = byTraineeResponseMapper;
    }

    @Autowired
    public void setByTrainerResponseMapper(ToDTOMapper<Training, GetTrainingsByTrainerResponse> byTrainerResponseMapper) {
        this.byTrainerResponseMapper = byTrainerResponseMapper;
    }
}

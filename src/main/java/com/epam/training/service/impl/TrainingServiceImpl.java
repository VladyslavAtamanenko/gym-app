package com.epam.training.service.impl;

import com.epam.training.dao.TraineeDao;
import com.epam.training.dao.TrainerDao;
import com.epam.training.dao.TrainingDao;
import com.epam.training.dto.TrainingCreateRequest;
import com.epam.training.dto.TrainingDTO;
import com.epam.training.mapper.Mapper;
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
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TrainingServiceImpl implements TrainingService {

    private static final Log LOGGER = LogFactory.getLog(TrainingServiceImpl.class);

    ToEntityMapper<TrainingCreateRequest, Training> trainingCreateRequestMapper;

    Mapper<Training, TrainingDTO> trainingMapper;

    @Autowired
    private TrainingDao trainingDao;

    @Autowired
    private TraineeDao traineeDao;

    @Autowired
    private TrainerDao trainerDao;

    @Override
    public TrainingDTO create(TrainingCreateRequest training) {
        if (training == null) {
            LOGGER.warn("Rejected training creation request because request body is null");
            throw new IllegalArgumentException();
        }

        Long traineeId = training.getTraineeId();
        Long trainerId = training.getTrainerId();
        LOGGER.debug("Creating training. traineeId=" + traineeId + ", trainerId=" + trainerId);

        Trainee trainee = traineeDao
                .findById(traineeId)
                .orElseThrow(() -> {
                    LOGGER.warn("Training creation failed because trainee was not found. traineeId=" + traineeId);
                    return new NoSuchElementException();
                });

        Trainer trainer = trainerDao
                .findById(trainerId)
                .orElseThrow(() -> {
                    LOGGER.warn("Training creation failed because trainer was not found. trainerId=" + trainerId);
                    return new NoSuchElementException();
                });

        Training created = trainingCreateRequestMapper.toEntity(training);

        created.setTrainee(trainee);
        created.setTrainer(trainer);

        Training saved = trainingDao.save(created);
        LOGGER.info("Training created successfully. trainingId=" + saved.getId()
                + ", traineeId=" + traineeId + ", trainerId=" + trainerId);
        return trainingMapper.toDTO(saved);
    }

    @Override
    public Optional<TrainingDTO> findById(Long id) {
        Optional<TrainingDTO> result = trainingDao.findById(id).map(trainingMapper::toDTO);
        LOGGER.debug("Training lookup completed. trainingId=" + id + ", found=" + result.isPresent());
        return result;
    }

    @Override
    public List<TrainingDTO> findAll() {
        List<TrainingDTO> result = trainingDao.findAll()
                .stream()
                .map(trainingMapper::toDTO)
                .collect(Collectors.toList());
        LOGGER.debug("Training list retrieved. count=" + result.size());
        return result;
    }

    @Autowired
    public void setTrainingCreateRequestMapper(ToEntityMapper<TrainingCreateRequest, Training> trainingCreateRequestMapper) {
        this.trainingCreateRequestMapper = trainingCreateRequestMapper;
    }

    @Autowired
    public void setTrainingMapper(Mapper<Training, TrainingDTO> trainingMapper) {
        this.trainingMapper = trainingMapper;
    }
}

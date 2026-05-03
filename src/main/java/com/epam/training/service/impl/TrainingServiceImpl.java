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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TrainingServiceImpl implements TrainingService {

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

        Long traineeId = training.getTraineeId();
        Long trainerId = training.getTrainerId();

        Trainee trainee = traineeDao
                .findById(traineeId)
                .orElseThrow(NoSuchElementException::new);

        Trainer trainer = trainerDao
                .findById(trainerId)
                .orElseThrow(NoSuchElementException::new);

        Training created = trainingCreateRequestMapper.toEntity(training);

        created.setTrainee(trainee);
        created.setTrainer(trainer);

        return trainingMapper.toDTO(trainingDao.save(created));
    }

    @Override
    public Optional<TrainingDTO> findById(Long id) {
        return trainingDao.findById(id).map(trainingMapper::toDTO);
    }

    @Override
    public List<TrainingDTO> findAll() {
        return trainingDao.findAll()
                .stream()
                .map(trainingMapper::toDTO)
                .collect(Collectors.toList());
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
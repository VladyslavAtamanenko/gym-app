package com.epam.training.service;

import com.epam.training.dto.TraineeDTO;
import com.epam.training.dto.TrainingCreateRequest;
import com.epam.training.dto.TrainingDTO;
import com.epam.training.model.Training;

import java.util.List;
import java.util.Optional;

public interface TrainingService {
    TrainingDTO create(TrainingCreateRequest training);


    Optional<TrainingDTO> findById(Long id);

    List<TrainingDTO> findAll();
}

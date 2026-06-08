package com.epam.training.service;

import com.epam.training.dto.*;

import java.util.List;

public interface TrainingService {

    Boolean create(TrainingCreateRequest training);

    List<GetTrainingsByTraineeResponse> findByTrainee(GetTrainingsByTraineeRequest request);

    List<GetTrainingsByTrainerResponse> findByTrainer(GetTrainingsByTrainerRequest request);
}

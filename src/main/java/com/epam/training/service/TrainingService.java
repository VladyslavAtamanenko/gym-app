package com.epam.training.service;

import com.epam.training.dto.GetTrainingsByTraineeResponse;
import com.epam.training.dto.GetTrainingsByTrainerResponse;
import com.epam.training.dto.TrainingCreateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface TrainingService {

    Boolean create(TrainingCreateRequest training);

    Page<GetTrainingsByTraineeResponse> findByTrainee(String username, LocalDate from, LocalDate to,
                                                      String trainerName, String trainingType,
                                                      Pageable pageable);

    Page<GetTrainingsByTrainerResponse> findByTrainer(String username, LocalDate from, LocalDate to,
                                                      String traineeName, Pageable pageable);
}

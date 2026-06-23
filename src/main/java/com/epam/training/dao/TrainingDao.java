package com.epam.training.dao;

import com.epam.training.model.Training;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface TrainingDao {

    Training save(Training training);

    Page<Training> findByTrainer(String trainerUsername, LocalDate from, LocalDate to,
                                 String traineeUsername, Pageable pageable);

    Page<Training> findByTrainee(String traineeUsername, String trainingType, LocalDate from,
                                 LocalDate to, String trainerUsername, Pageable pageable);
}

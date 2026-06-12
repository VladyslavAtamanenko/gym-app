package com.epam.training.dao;

import com.epam.training.model.Training;

import java.time.LocalDate;
import java.util.List;

public interface TrainingDao {

    Training save(Training training);

    List<Training> findByTrainer(String trainerUsername, LocalDate from, LocalDate to, String traineeUsername);

    List<Training> findByTrainee(String traineeUsername, String trainingType, LocalDate from, LocalDate to, String trainerUsername);

}

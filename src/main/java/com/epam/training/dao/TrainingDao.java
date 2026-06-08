package com.epam.training.dao;

import com.epam.training.model.Training;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TrainingDao {

    Training save(Training training);

    List<Training> findByTrainer(String trainerUsername, LocalDateTime from, LocalDateTime to, String traineeUsername);

    List<Training> findByTrainee(String traineeUsername, String trainingType, LocalDateTime from, LocalDateTime to, String trainerUsername);

}

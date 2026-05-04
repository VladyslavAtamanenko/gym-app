package com.epam.training.dao;

import com.epam.training.model.Training;

import java.util.List;
import java.util.Optional;

public interface TrainingDao {

    Training save(Training training);

    Optional<Training> findById(Long id);

    List<Training> findAll();

    List<Training> findAllByTrainee(Long traineeId);

    void delete(Long id);
}

package com.epam.training.dao;

import com.epam.training.model.Training;

import java.util.List;
import java.util.Optional;

public interface TrainingDao {

    void save(Training trainee);

    Optional<Training> findById(Long id);

    List<Training> findAll();
}

package com.epam.training.dao;

import com.epam.training.model.Training;

import java.util.List;

public interface TrainingDao {

    void save(Training trainee);

    Training findById(Long id);

    List<Training> findAll();
}

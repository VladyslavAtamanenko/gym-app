package com.epam.training.dao;

import com.epam.training.model.TrainingType;

import java.util.List;

public interface TrainingTypeDao {

    TrainingType save();

    TrainingType findById(Long id);

    TrainingType findByName(String name);

    List<TrainingType> findAll();
}

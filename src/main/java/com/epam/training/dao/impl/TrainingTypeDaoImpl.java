package com.epam.training.dao.impl;

import com.epam.training.dao.TrainingTypeDao;
import com.epam.training.model.TrainingType;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TrainingTypeDaoImpl implements TrainingTypeDao {
    @Override
    public TrainingType save() {
        return null;
    }

    @Override
    public TrainingType findById(Long id) {
        return null;
    }

    @Override
    public TrainingType findByName(String name) {
        return null;
    }

    @Override
    public List<TrainingType> findAll() {
        return List.of();
    }
}

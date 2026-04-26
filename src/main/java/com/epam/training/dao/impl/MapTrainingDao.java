package com.epam.training.dao.impl;

import com.epam.training.dao.TrainingDao;
import com.epam.training.model.Training;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class MapTrainingDao implements TrainingDao {

    @Autowired
    Map<Long, Training> storage;

    @Override
    public void save(Training training) {
        if (training == null) throw new IllegalArgumentException("Training is null");
        if (training.getId() == null) throw new IllegalArgumentException("ID is null");
        if (storage.containsKey(training.getId())) throw new IllegalArgumentException("Duplicate ID");
        storage.put(training.getId(), training);
    }

    @Override
    public Optional<Training> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Training> findAll() {
        return new ArrayList<>(storage.values());
    }


}

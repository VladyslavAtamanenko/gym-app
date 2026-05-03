package com.epam.training.dao.impl;

import com.epam.training.dao.TrainingDao;
import com.epam.training.model.Training;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class MapTrainingDao implements TrainingDao {

    private static long currentId = 1;

    @Autowired
    Map<Long, Training> storage;

    @Override
    public Training save(Training training) {

        if (training == null) throw new IllegalArgumentException("Training is null");

        Long trainingId;

        if (training.getId() == null) {
            trainingId = currentId++;
            training.setId(trainingId);
        }
        else {
            trainingId = training.getId();
        }

        storage.put(trainingId, training);

        return storage.get(trainingId);
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

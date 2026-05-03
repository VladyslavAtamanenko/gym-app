package com.epam.training.dao.impl;

import com.epam.training.dao.TrainerDao;
import com.epam.training.model.Trainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class MapTrainerDao implements TrainerDao {

    private static long currentId = 1;

    @Autowired
    Map<Long, Trainer> storage;

    @Override
    public Trainer save(Trainer trainer) {
        if (trainer == null) throw new IllegalArgumentException("Trainer is null");

        Long trainerId;

        if (trainer.getId() == null) {
            trainerId = currentId++;
            trainer.setId(trainerId);
        }
        else {
            trainerId = trainer.getId();
        }

        storage.put(trainerId, trainer);

        return storage.get(trainerId);
    }

    @Override
    public Optional<Trainer> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Trainer> findAll() {
        return new ArrayList<>(storage.values());
    }
}

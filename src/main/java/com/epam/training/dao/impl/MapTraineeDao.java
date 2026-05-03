package com.epam.training.dao.impl;

import com.epam.training.dao.TraineeDao;
import com.epam.training.model.Trainee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class MapTraineeDao implements TraineeDao {

    private static long currentId = 1;

    @Autowired
    Map<Long, Trainee> storage;

    @Override
    public Trainee save(Trainee trainee) {

        if (trainee == null) throw new IllegalArgumentException("Trainee is null");

        Long traineeId;

        if (trainee.getId() == null) {
            traineeId = currentId++;
            trainee.setId(traineeId);
        }
        else {
            traineeId = trainee.getId();
        }

        storage.put(traineeId, trainee);

        return storage.get(traineeId);
    }

    @Override
    public Optional<Trainee> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Trainee> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public void delete(Long id) {
        storage.remove(id);
    }
}

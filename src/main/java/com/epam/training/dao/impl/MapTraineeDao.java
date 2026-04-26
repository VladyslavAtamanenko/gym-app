package com.epam.training.dao.impl;

import com.epam.training.dao.TraineeDao;
import com.epam.training.model.Trainee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class MapTraineeDao implements TraineeDao {

    @Autowired
    Map<Long, Trainee> storage;

    @Override
    public void save(Trainee trainee) {
        if (trainee == null) throw new IllegalArgumentException("Trainee is null");
        if (trainee.getId() == null) throw new IllegalArgumentException("ID is null");
        if (storage.containsKey(trainee.getId())) throw new IllegalArgumentException("Duplicate ID");
        storage.put(trainee.getId(), trainee);
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
    public void update(Trainee trainee) {
        if (trainee == null) throw new IllegalArgumentException("Trainee is null");
        if (trainee.getId() == null) throw new IllegalArgumentException("ID is null");
        if (!storage.containsKey(trainee.getId())) throw new NoSuchElementException("Trainee not found");
        storage.put(trainee.getId(), trainee);
    }

    @Override
    public void delete(Long id) {
        storage.remove(id);
    }
}

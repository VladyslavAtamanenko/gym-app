package com.epam.training.dao.impl;

import com.epam.training.dao.TrainerDao;
import com.epam.training.model.Trainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class MapTrainerDao implements TrainerDao {

    @Autowired
    Map<Long, Trainer> storage;

    @Override
    public void save(Trainer trainer) {
        if (trainer == null) throw new IllegalArgumentException("Trainer is null");
        if (trainer.getId() == null) throw new IllegalArgumentException("ID is null");
        if (storage.containsKey(trainer.getId())) throw new IllegalArgumentException("Duplicate ID");
        storage.put(trainer.getId(), trainer);
    }

    @Override
    public Optional<Trainer> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Trainer> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public void update(Trainer trainer) {
        if (trainer == null) throw new IllegalArgumentException("Trainer is null");
        if (trainer.getId() == null) throw new IllegalArgumentException("ID is null");
        if (!storage.containsKey(trainer.getId())) throw new NoSuchElementException("Trainer not found");
        storage.put(trainer.getId(), trainer);
    }

}

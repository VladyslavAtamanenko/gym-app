package com.epam.training.dao;

import com.epam.training.model.Trainer;

import java.util.List;
import java.util.Optional;

public interface TrainerDao {

    Trainer save(Trainer trainer);

    List<Trainer> findAll();

    Optional<Trainer> findByUsername(String username);

    List<Trainer> findNotAssignedOnTrainee(String traineeUsername);
}

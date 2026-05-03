package com.epam.training.dao;

import com.epam.training.model.Trainee;

import java.util.List;
import java.util.Optional;

public interface TraineeDao {

    Trainee save(Trainee trainee);

    Optional<Trainee> findById(Long id);

    List<Trainee> findAll();

    void delete(Long id);
}

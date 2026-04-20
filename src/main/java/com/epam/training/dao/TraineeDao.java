package com.epam.training.dao;

import com.epam.training.model.Trainee;

import java.util.List;

public interface TraineeDao {

    void save(Trainee trainee);

    Trainee findById(Long id);

    List<Trainee> findAll();

    void update(Trainee trainee);

    void delete(Long id);
}

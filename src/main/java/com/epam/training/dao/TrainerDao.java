package com.epam.training.dao;

import com.epam.training.model.Trainer;

import java.util.List;
import java.util.Optional;

public interface TrainerDao {

    Trainer save(Trainer trainer);

    Optional<Trainer> findById(Long id);

    List<Trainer> findAll();

}

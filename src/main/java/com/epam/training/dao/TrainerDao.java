package com.epam.training.dao;

import com.epam.training.model.Trainer;

import java.util.List;

public interface TrainerDao {

    void save(Trainer trainer);

    Trainer findById(Long id);

    List<Trainer> findAll();

    void update(Trainer trainer);
}

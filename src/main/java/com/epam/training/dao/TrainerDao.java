package com.epam.training.dao;

import com.epam.training.model.Trainer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface TrainerDao {

    Trainer save(Trainer trainer);

    List<Trainer> findAll();

    Optional<Trainer> findByUsername(String username);

    Page<Trainer> findNotAssignedOnTrainee(String traineeUsername, Pageable pageable);
}

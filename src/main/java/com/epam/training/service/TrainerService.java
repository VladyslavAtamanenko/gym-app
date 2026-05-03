package com.epam.training.service;

import com.epam.training.dto.TrainerCreateRequest;
import com.epam.training.dto.TrainerCreateResponse;
import com.epam.training.dto.TrainerDTO;

import java.util.List;
import java.util.Optional;

public interface TrainerService {
    TrainerCreateResponse create(TrainerCreateRequest trainer);

    TrainerDTO update(TrainerDTO trainer);

    Optional<TrainerDTO> findById(Long id);

    List<TrainerDTO> findAll();
}

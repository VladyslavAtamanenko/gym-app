package com.epam.training.service;

import com.epam.training.dto.TraineeCreateRequest;
import com.epam.training.dto.TraineeCreateResponse;
import com.epam.training.dto.TraineeDTO;

import java.util.List;
import java.util.Optional;

public interface TraineeService {

    TraineeCreateResponse create(TraineeCreateRequest trainee);

    TraineeDTO update(TraineeDTO trainee);

    void delete(Long id);

    Optional<TraineeDTO> findById(Long id);

    List<TraineeDTO> findAll();
}


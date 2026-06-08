package com.epam.training.service;

import com.epam.training.dto.*;

import java.util.List;
import java.util.Optional;

public interface TrainerService {
    TrainerCreateResponse create(TrainerCreateRequest trainer);

    Boolean credentialsMatch(LoginRequest credentials);

    Boolean changePassword(ChangeLoginRequest request);

    TrainerUpdateResponse update(TrainerUpdateRequest trainer);

    Optional<TrainerGetResponse> findByUsername(String username);

    List<TrainerDTO> findNotAssignedOnTrainee(String traineeUsername);

    List<TrainerGetResponse> findAll();
}

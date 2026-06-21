package com.epam.training.service;

import com.epam.training.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TrainerService {
    TrainerCreateResponse create(TrainerCreateRequest trainer);

    Boolean credentialsMatch(LoginRequest credentials);

    Boolean changePassword(ChangeLoginRequest request);

    TrainerUpdateResponse update(String username, TrainerUpdateRequest trainer);

    TrainerGetResponse activate(String username);

    TrainerGetResponse deactivate(String username);

    TrainerGetResponse findByUsername(String username);

    Page<TrainerDTO> findNotAssignedOnTrainee(String traineeUsername, Pageable pageable);

    List<TrainerGetResponse> findAll();
}

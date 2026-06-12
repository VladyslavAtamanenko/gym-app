package com.epam.training.service;

import com.epam.training.dto.*;

import java.util.List;
import java.util.Optional;

public interface TraineeService {

    TraineeCreateResponse create(TraineeCreateRequest trainee);

    Boolean credentialsMatch(LoginRequest credentials);

    Boolean changePassword(ChangeLoginRequest request);

    TraineeUpdateResponse update(TraineeUpdateRequest trainee);

    TraineeGetResponse activate(String username);

    TraineeGetResponse deactivate(String username);

    List<TrainerDTO> updateTrainersList(TraineeUpdateTrainersRequest request);

    void delete(String username);

    Optional<TraineeGetResponse> findByUsername(String username);

    List<TraineeGetResponse> findAll();
}


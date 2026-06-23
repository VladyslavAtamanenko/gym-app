package com.epam.training.service;

import com.epam.training.dto.*;

import java.util.List;

public interface TraineeService {

    TraineeCreateResponse create(TraineeCreateRequest trainee);

    Boolean credentialsMatch(LoginRequest credentials);

    Boolean changePassword(ChangeLoginRequest request);

    TraineeUpdateResponse update(String username, TraineeUpdateRequest trainee);

    TraineeGetResponse activate(String username);

    TraineeGetResponse deactivate(String username);

    List<TrainerDTO> updateTrainersList(String username, TraineeUpdateTrainersRequest request);

    void delete(String username);

    TraineeGetResponse findByUsername(String username);

    List<TraineeGetResponse> findAll();
}


package com.epam.training.facade;

import com.epam.training.dto.*;
import com.epam.training.service.TraineeService;
import com.epam.training.service.TrainerService;
import com.epam.training.service.TrainingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GymApp {

    private static final Logger log = LoggerFactory.getLogger(GymApp.class);

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;

    @Autowired
    public GymApp(TraineeService traineeService, TrainerService trainerService, TrainingService trainingService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
    }

    public TraineeCreateResponse createTrainee(TraineeCreateRequest trainee){
        log.debug("Facade request received: create trainee");
        return traineeService.create(trainee);
    }

    public Boolean loginTrainee(LoginRequest loginRequest) {
        log.debug("Facade request received: login trainee. username={}", loginRequest == null ? null : loginRequest.getUsername());
        return traineeService.credentialsMatch(loginRequest);
    }

    public Boolean changeTraineePassword(LoginRequest loginRequest, ChangeLoginRequest request) {
        ensureTraineeLoggedIn(loginRequest);
        requireOwnership(loginRequest, request == null ? null : request.getUsername());
        log.debug("Facade request received: change trainee password. username={}", request == null ? null : request.getUsername());
        return traineeService.changePassword(request);
    }

    public TraineeUpdateResponse updateTrainee(LoginRequest loginRequest, String username, TraineeUpdateRequest trainee){
        ensureTraineeLoggedIn(loginRequest);
        requireOwnership(loginRequest, username);
        log.debug("Facade request received: update trainee. traineeUsername={}", username);
        return traineeService.update(username, trainee);
    }

    public TraineeGetResponse activateTrainee(LoginRequest loginRequest, String username) {
        ensureTraineeLoggedIn(loginRequest);
        requireOwnership(loginRequest, username);
        log.debug("Facade request received: activate trainee. traineeUsername={}", username);
        return traineeService.activate(username);
    }

    public TraineeGetResponse deactivateTrainee(LoginRequest loginRequest, String username) {
        ensureTraineeLoggedIn(loginRequest);
        requireOwnership(loginRequest, username);
        log.debug("Facade request received: deactivate trainee. traineeUsername={}", username);
        return traineeService.deactivate(username);
    }

    public List<TrainerDTO> updateTraineeTrainers(LoginRequest loginRequest, String username, TraineeUpdateTrainersRequest request) {
        ensureTraineeLoggedIn(loginRequest);
        requireOwnership(loginRequest, username);
        log.debug("Facade request received: update trainee trainers. traineeUsername={}", username);
        return traineeService.updateTrainersList(username, request);
    }

    public void deleteTrainee(LoginRequest loginRequest, String username){
        ensureTraineeLoggedIn(loginRequest);
        requireOwnership(loginRequest, username);
        log.debug("Facade request received: delete trainee. traineeUsername={}", username);
        traineeService.delete(username);
    }

    public TraineeGetResponse findTraineeByUsername(LoginRequest loginRequest, String username){
        ensureTraineeLoggedIn(loginRequest);
        requireOwnership(loginRequest, username);
        log.debug("Facade request received: find trainee. traineeUsername={}", username);
        return traineeService.findByUsername(username);
    }

    public List<TraineeGetResponse> findAllTrainees(LoginRequest loginRequest){
        ensureTraineeLoggedIn(loginRequest);
        log.debug("Facade request received: list trainees");
        return traineeService.findAll();
    }

    public TrainerCreateResponse createTrainer(TrainerCreateRequest trainer){
        log.debug("Facade request received: create trainer");
        return trainerService.create(trainer);
    }

    public Boolean loginTrainer(LoginRequest loginRequest) {
        log.debug("Facade request received: login trainer. username={}", loginRequest == null ? null : loginRequest.getUsername());
        return trainerService.credentialsMatch(loginRequest);
    }

    public Boolean changeTrainerPassword(LoginRequest loginRequest, ChangeLoginRequest request) {
        ensureTrainerLoggedIn(loginRequest);
        requireOwnership(loginRequest, request == null ? null : request.getUsername());
        log.debug("Facade request received: change trainer password. username={}", request == null ? null : request.getUsername());
        return trainerService.changePassword(request);
    }

    public TrainerUpdateResponse updateTrainer(LoginRequest loginRequest, String username, TrainerUpdateRequest trainer){
        ensureTrainerLoggedIn(loginRequest);
        requireOwnership(loginRequest, username);
        log.debug("Facade request received: update trainer. trainerUsername={}", username);
        return trainerService.update(username, trainer);
    }

    public TrainerGetResponse activateTrainer(LoginRequest loginRequest, String username) {
        ensureTrainerLoggedIn(loginRequest);
        requireOwnership(loginRequest, username);
        log.debug("Facade request received: activate trainer. trainerUsername={}", username);
        return trainerService.activate(username);
    }

    public TrainerGetResponse deactivateTrainer(LoginRequest loginRequest, String username) {
        ensureTrainerLoggedIn(loginRequest);
        requireOwnership(loginRequest, username);
        log.debug("Facade request received: deactivate trainer. trainerUsername={}", username);
        return trainerService.deactivate(username);
    }

    public TrainerGetResponse findTrainerByUsername(LoginRequest loginRequest, String username){
        ensureTrainerLoggedIn(loginRequest);
        requireOwnership(loginRequest, username);
        log.debug("Facade request received: find trainer. trainerUsername={}", username);
        return trainerService.findByUsername(username);
    }

    public Page<TrainerDTO> findNotAssignedTrainers(LoginRequest loginRequest, String traineeUsername,
                                                    Pageable pageable) {
        ensureTraineeLoggedIn(loginRequest);
        requireOwnership(loginRequest, traineeUsername);
        log.debug("Facade request received: find not assigned trainers. traineeUsername={}", traineeUsername);
        return trainerService.findNotAssignedOnTrainee(traineeUsername, pageable);
    }

    public List<TrainerGetResponse> findAllTrainers(LoginRequest loginRequest){
        ensureTrainerLoggedIn(loginRequest);
        log.debug("Facade request received: list trainers");
        return trainerService.findAll();
    }

    public Boolean createTraining(LoginRequest loginRequest, TrainingCreateRequest training){
        ensureTrainerLoggedIn(loginRequest);
        requireOwnership(loginRequest, training == null ? null : training.getTrainer());
        log.debug("Facade request received: create training. traineeUsername={}, trainerUsername={}",
                training == null ? null : training.getTrainee(),
                training == null ? null : training.getTrainer());
        return trainingService.create(training);
    }

    public Page<GetTrainingsByTraineeResponse> findTrainingsByTrainee(LoginRequest loginRequest,
                                                                       String username,
                                                                       java.time.LocalDate from,
                                                                       java.time.LocalDate to,
                                                                       String trainerName,
                                                                       String trainingType,
                                                                       Pageable pageable) {
        ensureTraineeLoggedIn(loginRequest);
        requireOwnership(loginRequest, username);
        log.debug("Facade request received: list trainings by trainee. traineeUsername={}", username);
        return trainingService.findByTrainee(username, from, to, trainerName, trainingType, pageable);
    }

    public Page<GetTrainingsByTrainerResponse> findTrainingsByTrainer(LoginRequest loginRequest,
                                                                       String username,
                                                                       java.time.LocalDate from,
                                                                       java.time.LocalDate to,
                                                                       String traineeName,
                                                                       Pageable pageable) {
        ensureTrainerLoggedIn(loginRequest);
        requireOwnership(loginRequest, username);
        log.debug("Facade request received: list trainings by trainer. trainerUsername={}", username);
        return trainingService.findByTrainer(username, from, to, traineeName, pageable);
    }

    private void ensureTraineeLoggedIn(LoginRequest loginRequest) {
        if (!Boolean.TRUE.equals(traineeService.credentialsMatch(loginRequest))) {
            throw new SecurityException("Invalid trainee credentials");
        }
    }

    private void ensureTrainerLoggedIn(LoginRequest loginRequest) {
        if (!Boolean.TRUE.equals(trainerService.credentialsMatch(loginRequest))) {
            throw new SecurityException("Invalid trainer credentials");
        }
    }

    private void requireOwnership(LoginRequest loginRequest, String targetUsername) {
        if (loginRequest == null || !loginRequest.getUsername().equals(targetUsername)) {
            log.warn("Ownership check failed: logged-in user '{}' attempted to access data of '{}'",
                    loginRequest == null ? null : loginRequest.getUsername(), targetUsername);
            throw new SecurityException("Access denied: you can only manage your own data");
        }
    }
}

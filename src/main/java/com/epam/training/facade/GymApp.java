package com.epam.training.facade;

import com.epam.training.dto.*;
import com.epam.training.service.TraineeService;
import com.epam.training.service.TrainerService;
import com.epam.training.service.TrainingService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class GymApp {

    private static final Log LOGGER = LogFactory.getLog(GymApp.class);

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
        LOGGER.debug("Facade request received: create trainee");
        return traineeService.create(trainee);
    }

    public Boolean loginTrainee(LoginRequest loginRequest) {
        LOGGER.debug("Facade request received: login trainee. username="
                + (loginRequest == null ? null : loginRequest.getUsername()));
        return traineeService.credentialsMatch(loginRequest);
    }

    public Boolean changeTraineePassword(LoginRequest loginRequest, ChangeLoginRequest request) {
        ensureTraineeLoggedIn(loginRequest);
        LOGGER.debug("Facade request received: change trainee password. username="
                + (request == null ? null : request.getUsername()));
        return traineeService.changePassword(request);
    }

    public TraineeUpdateResponse updateTrainee(LoginRequest loginRequest, TraineeUpdateRequest trainee){
        ensureTraineeLoggedIn(loginRequest);
        LOGGER.debug("Facade request received: update trainee. traineeUsername="
                + (trainee == null ? null : trainee.getUsername()));
        return traineeService.update(trainee);
    }

    public TraineeGetResponse activateTrainee(LoginRequest loginRequest, String username) {
        ensureTraineeLoggedIn(loginRequest);
        LOGGER.debug("Facade request received: activate trainee. traineeUsername=" + username);
        return traineeService.activate(username);
    }

    public TraineeGetResponse deactivateTrainee(LoginRequest loginRequest, String username) {
        ensureTraineeLoggedIn(loginRequest);
        LOGGER.debug("Facade request received: deactivate trainee. traineeUsername=" + username);
        return traineeService.deactivate(username);
    }

    public List<TrainerDTO> updateTraineeTrainers(LoginRequest loginRequest, TraineeUpdateTrainersRequest request) {
        ensureTraineeLoggedIn(loginRequest);
        LOGGER.debug("Facade request received: update trainee trainers. traineeUsername="
                + (request == null ? null : request.getUsername()));
        return traineeService.updateTrainersList(request);
    }

    public void deleteTrainee(LoginRequest loginRequest, String username){
        ensureTraineeLoggedIn(loginRequest);
        LOGGER.debug("Facade request received: delete trainee. traineeUsername=" + username);
        traineeService.delete(username);
    }

    public Optional<TraineeGetResponse> findTraineeByUsername(LoginRequest loginRequest, String username){
        ensureTraineeLoggedIn(loginRequest);
        LOGGER.debug("Facade request received: find trainee. traineeUsername=" + username);
        return traineeService.findByUsername(username);
    }

    public List<TraineeGetResponse> findAllTrainees(LoginRequest loginRequest){
        ensureTraineeLoggedIn(loginRequest);
        LOGGER.debug("Facade request received: list trainees");
        return traineeService.findAll();
    }

    public TrainerCreateResponse createTrainer(TrainerCreateRequest trainer){
        LOGGER.debug("Facade request received: create trainer");
        return trainerService.create(trainer);
    }

    public Boolean loginTrainer(LoginRequest loginRequest) {
        LOGGER.debug("Facade request received: login trainer. username="
                + (loginRequest == null ? null : loginRequest.getUsername()));
        return trainerService.credentialsMatch(loginRequest);
    }

    public Boolean changeTrainerPassword(LoginRequest loginRequest, ChangeLoginRequest request) {
        ensureTrainerLoggedIn(loginRequest);
        LOGGER.debug("Facade request received: change trainer password. username="
                + (request == null ? null : request.getUsername()));
        return trainerService.changePassword(request);
    }

    public TrainerUpdateResponse updateTrainer(LoginRequest loginRequest, TrainerUpdateRequest trainer){
        ensureTrainerLoggedIn(loginRequest);
        LOGGER.debug("Facade request received: update trainer. trainerUsername="
                + (trainer == null ? null : trainer.getUsername()));
        return trainerService.update(trainer);
    }

    public TrainerGetResponse activateTrainer(LoginRequest loginRequest, String username) {
        ensureTrainerLoggedIn(loginRequest);
        LOGGER.debug("Facade request received: activate trainer. trainerUsername=" + username);
        return trainerService.activate(username);
    }

    public TrainerGetResponse deactivateTrainer(LoginRequest loginRequest, String username) {
        ensureTrainerLoggedIn(loginRequest);
        LOGGER.debug("Facade request received: deactivate trainer. trainerUsername=" + username);
        return trainerService.deactivate(username);
    }

    public Optional<TrainerGetResponse> findTrainerByUsername(LoginRequest loginRequest, String username){
        ensureTrainerLoggedIn(loginRequest);
        LOGGER.debug("Facade request received: find trainer. trainerUsername=" + username);
        return trainerService.findByUsername(username);
    }

    public List<TrainerDTO> findNotAssignedTrainers(LoginRequest loginRequest, String traineeUsername) {
        ensureTraineeLoggedIn(loginRequest);
        LOGGER.debug("Facade request received: find not assigned trainers. traineeUsername=" + traineeUsername);
        return trainerService.findNotAssignedOnTrainee(traineeUsername);
    }

    public List<TrainerGetResponse> findAllTrainers(LoginRequest loginRequest){
        ensureTrainerLoggedIn(loginRequest);
        LOGGER.debug("Facade request received: list trainers");
        return trainerService.findAll();
    }

    public Boolean createTraining(LoginRequest loginRequest, TrainingCreateRequest training){
        ensureTrainerLoggedIn(loginRequest);
        LOGGER.debug("Facade request received: create training. traineeUsername="
                + (training == null ? null : training.getTrainee())
                + ", trainerUsername=" + (training == null ? null : training.getTrainer()));
        return trainingService.create(training);
    }

    public List<GetTrainingsByTraineeResponse> findTrainingsByTrainee(LoginRequest loginRequest,
                                                                       GetTrainingsByTraineeRequest request) {
        ensureTraineeLoggedIn(loginRequest);
        LOGGER.debug("Facade request received: list trainings by trainee. traineeUsername="
                + (request == null ? null : request.getUsername()));
        return trainingService.findByTrainee(request);
    }

    public List<GetTrainingsByTrainerResponse> findTrainingsByTrainer(LoginRequest loginRequest,
                                                                       GetTrainingsByTrainerRequest request) {
        ensureTrainerLoggedIn(loginRequest);
        LOGGER.debug("Facade request received: list trainings by trainer. trainerUsername="
                + (request == null ? null : request.getUsername()));
        return trainingService.findByTrainer(request);
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
}

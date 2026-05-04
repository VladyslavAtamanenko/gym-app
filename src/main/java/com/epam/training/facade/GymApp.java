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

    public TraineeDTO updateTrainee(TraineeDTO trainee){
        LOGGER.debug("Facade request received: update trainee. traineeId="
                + (trainee == null ? null : trainee.getId()));
        return traineeService.update(trainee);
    }

    public void deleteTrainee(Long id){
        LOGGER.debug("Facade request received: delete trainee. traineeId=" + id);
        traineeService.delete(id);
    }

    public Optional<TraineeDTO> findTraineeById(Long id){
        LOGGER.debug("Facade request received: find trainee. traineeId=" + id);
        return traineeService.findById(id);
    }

    public List<TraineeDTO> findAllTrainees(){
        LOGGER.debug("Facade request received: list trainees");
        return traineeService.findAll();
    }

    public TrainerCreateResponse createTrainer(TrainerCreateRequest trainer){
        LOGGER.debug("Facade request received: create trainer");
        return trainerService.create(trainer);
    }

    public TrainerDTO updateTrainer(TrainerDTO trainer){
        LOGGER.debug("Facade request received: update trainer. trainerId="
                + (trainer == null ? null : trainer.getId()));
        return trainerService.update(trainer);
    }

    public Optional<TrainerDTO> findTrainerById(Long id){
        LOGGER.debug("Facade request received: find trainer. trainerId=" + id);
        return trainerService.findById(id);
    }

    public List<TrainerDTO> findAllTrainers(){
        LOGGER.debug("Facade request received: list trainers");
        return trainerService.findAll();
    }

    public TrainingDTO createTraining(TrainingCreateRequest training){
        LOGGER.debug("Facade request received: create training. traineeId="
                + (training == null ? null : training.getTraineeId())
                + ", trainerId=" + (training == null ? null : training.getTrainerId()));
        return trainingService.create(training);
    }

    public Optional<TrainingDTO> findTrainingById(Long id){
        LOGGER.debug("Facade request received: find training. trainingId=" + id);
        return trainingService.findById(id);
    }

    public List<TrainingDTO> findAllTrainings(){
        LOGGER.debug("Facade request received: list trainings");
        return trainingService.findAll();
    }
}

package com.epam.training.facade;

import com.epam.training.dto.*;
import com.epam.training.service.TraineeService;
import com.epam.training.service.TrainerService;
import com.epam.training.service.TrainingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class GymApp {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;

    @Autowired
    public GymApp(TraineeService traineeService, TrainerService trainerService, TrainingService trainingService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
    }

    TraineeCreateResponse createTrainee(TraineeCreateRequest trainee){
        return traineeService.create(trainee);
    }

    TraineeDTO updateTrainee(TraineeDTO trainee){
        return traineeService.update(trainee);
    }

    void deleteTrainee(Long id){
        traineeService.delete(id);
    }

    Optional<TraineeDTO> findTraineeById(Long id){
        return traineeService.findById(id);
    }

    List<TraineeDTO> findAllTrainees(){
        return traineeService.findAll();
    }

    TrainerCreateResponse createTrainer(TrainerCreateRequest trainer){
        return trainerService.create(trainer);
    }

    TrainerDTO updateTrainer(TrainerDTO trainer){
        return trainerService.update(trainer);
    }

    Optional<TrainerDTO> findTrainerById(Long id){
        return trainerService.findById(id);
    }

    List<TrainerDTO> findAllTrainers(){
        return trainerService.findAll();
    }

    TrainingDTO createTraining(TrainingCreateRequest training){
        return trainingService.create(training);
    }

    Optional<TrainingDTO> findTrainingById(Long id){
        return trainingService.findById(id);
    }

    List<TrainingDTO> findAllTrainings(){
        return trainingService.findAll();
    }
}

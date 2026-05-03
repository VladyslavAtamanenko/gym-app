package com.epam.training.facade;

import com.epam.training.dto.*;
import com.epam.training.service.TraineeService;
import com.epam.training.service.TrainerService;
import com.epam.training.service.TrainingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GymAppTest {

    @Mock
    private TraineeService traineeService;

    @Mock
    private TrainerService trainerService;

    @Mock
    private TrainingService trainingService;

    @InjectMocks
    private GymApp gymApp;

    private TraineeCreateRequest traineeCreateRequest;
    private TraineeCreateResponse traineeCreateResponse;
    private TraineeDTO traineeDTO;

    private TrainerCreateRequest trainerCreateRequest;
    private TrainerCreateResponse trainerCreateResponse;
    private TrainerDTO trainerDTO;

    private TrainingCreateRequest trainingCreateRequest;
    private TrainingDTO trainingDTO;

    @BeforeEach
    void setUp() {
        traineeCreateRequest = new TraineeCreateRequest();
        traineeCreateResponse = new TraineeCreateResponse();
        traineeDTO = new TraineeDTO();

        trainerCreateRequest = new TrainerCreateRequest();
        trainerCreateResponse = new TrainerCreateResponse();
        trainerDTO = new TrainerDTO();

        trainingCreateRequest = new TrainingCreateRequest();
        trainingDTO = new TrainingDTO();
    }

    // ---------- Trainee ----------

    @Test
    void createTrainee_shouldDelegate() {
        when(traineeService.create(traineeCreateRequest)).thenReturn(traineeCreateResponse);

        var result = gymApp.createTrainee(traineeCreateRequest);

        verify(traineeService).create(traineeCreateRequest);
        verifyNoMoreInteractions(traineeService);
        assertEquals(traineeCreateResponse, result);
    }

    @Test
    void updateTrainee_shouldDelegate() {
        when(traineeService.update(traineeDTO)).thenReturn(traineeDTO);

        var result = gymApp.updateTrainee(traineeDTO);

        verify(traineeService).update(traineeDTO);
        verifyNoMoreInteractions(traineeService);
        assertEquals(traineeDTO, result);
    }

    @Test
    void deleteTrainee_shouldDelegate() {
        Long id = 1L;

        gymApp.deleteTrainee(id);

        verify(traineeService).delete(id);
        verifyNoMoreInteractions(traineeService);
    }

    @Test
    void findTraineeById_shouldDelegate() {
        Long id = 1L;
        when(traineeService.findById(id)).thenReturn(Optional.of(traineeDTO));

        var result = gymApp.findTraineeById(id);

        verify(traineeService).findById(id);
        verifyNoMoreInteractions(traineeService);
        assertTrue(result.isPresent());
        assertEquals(traineeDTO, result.get());
    }

    @Test
    void findAllTrainees_shouldDelegate() {
        List<TraineeDTO> list = List.of(traineeDTO);
        when(traineeService.findAll()).thenReturn(list);

        var result = gymApp.findAllTrainees();

        verify(traineeService).findAll();
        verifyNoMoreInteractions(traineeService);
        assertEquals(list, result);
    }

    // ---------- Trainer ----------

    @Test
    void createTrainer_shouldDelegate() {
        when(trainerService.create(trainerCreateRequest)).thenReturn(trainerCreateResponse);

        var result = gymApp.createTrainer(trainerCreateRequest);

        verify(trainerService).create(trainerCreateRequest);
        verifyNoMoreInteractions(trainerService);
        assertEquals(trainerCreateResponse, result);
    }

    @Test
    void updateTrainer_shouldDelegate() {
        when(trainerService.update(trainerDTO)).thenReturn(trainerDTO);

        var result = gymApp.updateTrainer(trainerDTO);

        verify(trainerService).update(trainerDTO);
        verifyNoMoreInteractions(trainerService);
        assertEquals(trainerDTO, result);
    }

    @Test
    void findTrainerById_shouldDelegate() {
        Long id = 1L;
        when(trainerService.findById(id)).thenReturn(Optional.of(trainerDTO));

        var result = gymApp.findTrainerById(id);

        verify(trainerService).findById(id);
        verifyNoMoreInteractions(trainerService);
        assertTrue(result.isPresent());
    }

    @Test
    void findAllTrainers_shouldDelegate() {
        List<TrainerDTO> list = List.of(trainerDTO);
        when(trainerService.findAll()).thenReturn(list);

        var result = gymApp.findAllTrainers();

        verify(trainerService).findAll();
        verifyNoMoreInteractions(trainerService);
        assertEquals(list, result);
    }

    // ---------- Training ----------

    @Test
    void createTraining_shouldDelegate() {
        when(trainingService.create(trainingCreateRequest)).thenReturn(trainingDTO);

        var result = gymApp.createTraining(trainingCreateRequest);

        verify(trainingService).create(trainingCreateRequest);
        verifyNoMoreInteractions(trainingService);
        assertEquals(trainingDTO, result);
    }

    @Test
    void findTrainingById_shouldDelegate() {
        Long id = 1L;
        when(trainingService.findById(id)).thenReturn(Optional.of(trainingDTO));

        var result = gymApp.findTrainingById(id);

        verify(trainingService).findById(id);
        verifyNoMoreInteractions(trainingService);
        assertTrue(result.isPresent());
    }

    @Test
    void findAllTrainings_shouldDelegate() {
        List<TrainingDTO> list = List.of(trainingDTO);
        when(trainingService.findAll()).thenReturn(list);

        var result = gymApp.findAllTrainings();

        verify(trainingService).findAll();
        verifyNoMoreInteractions(trainingService);
        assertEquals(list, result);
    }
}
package com.epam.training.service;


import com.epam.training.dao.TraineeDao;
import com.epam.training.dao.TrainerDao;
import com.epam.training.dao.TrainingDao;
import com.epam.training.dto.TrainingCreateRequest;
import com.epam.training.dto.TrainingDTO;
import com.epam.training.mapper.Mapper;
import com.epam.training.mapper.ToEntityMapper;
import com.epam.training.model.*;
import com.epam.training.service.impl.TrainingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingServiceTest {

    @Mock
    private TrainingDao trainingDao;

    @Mock
    private TraineeDao traineeDao;

    @Mock
    private TrainerDao trainerDao;

    @Mock
    private ToEntityMapper<TrainingCreateRequest, Training> trainingCreateRequestMapper;

    @Mock
    private Mapper<Training, TrainingDTO> trainingMapper;

    @InjectMocks
    private TrainingServiceImpl service;

    private static final TrainingType FITNESS = new TrainingType(1L, "Fitness");

    private Trainee trainee;
    private Trainer trainer;
    private Training training;
    private TrainingDTO trainingDTO;
    private TrainingCreateRequest createRequest;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUserName("John.Doe");
        user.setIsActive(true);

        trainee = new Trainee();
        trainee.setId(1L);
        trainee.setUser(user);

        trainer = new Trainer();
        trainer.setId(2L);
        trainer.setSpecialization(FITNESS);
        trainer.setUser(user);

        training = new Training();
        training.setId(10L);
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setName("Morning Run");
        training.setType(FITNESS);
        training.setDate(LocalDateTime.of(2025, 6, 1, 8, 0));
        training.setDuration(Duration.ofHours(1));

        trainingDTO = new TrainingDTO(10L, 1L, 2L, "Morning Run", FITNESS,
                LocalDateTime.of(2025, 6, 1, 8, 0), Duration.ofHours(1));

        createRequest = new TrainingCreateRequest(1L, 2L, "Morning Run", FITNESS,
                LocalDateTime.of(2025, 6, 1, 8, 0), Duration.ofHours(1));
    }


    @Test
    @DisplayName("create: should resolve trainee and trainer by ID, map, save and return DTO")
    void create_success() {
        when(traineeDao.findById(1L)).thenReturn(Optional.of(trainee));
        when(trainerDao.findById(2L)).thenReturn(Optional.of(trainer));
        when(trainingCreateRequestMapper.toEntity(createRequest)).thenReturn(training);
        when(trainingDao.save(training)).thenReturn(training);
        when(trainingMapper.toDTO(training)).thenReturn(trainingDTO);

        TrainingDTO result = service.create(createRequest);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals("Morning Run", result.getName());
        assertEquals(1L, result.getTraineeId());
        assertEquals(2L, result.getTrainerId());

        verify(traineeDao).findById(1L);
        verify(trainerDao).findById(2L);
        // verifies trainee and trainer were wired onto the entity before saving
        verify(trainingDao).save(training);
        assertEquals(trainee, training.getTrainee());
        assertEquals(trainer, training.getTrainer());
    }

    @Test
    @DisplayName("create: non-existent trainee ID should throw NoSuchElementException")
    void create_traineeNotFound_throws() {
        when(traineeDao.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> service.create(createRequest));

        verify(traineeDao).findById(1L);
        verifyNoInteractions(trainerDao, trainingCreateRequestMapper, trainingDao, trainingMapper);
    }

    @Test
    @DisplayName("create: non-existent trainer ID should throw NoSuchElementException")
    void create_trainerNotFound_throws() {
        when(traineeDao.findById(1L)).thenReturn(Optional.of(trainee));
        when(trainerDao.findById(2L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> service.create(createRequest));

        verify(traineeDao).findById(1L);
        verify(trainerDao).findById(2L);
        verifyNoInteractions(trainingCreateRequestMapper, trainingDao, trainingMapper);
    }


    @Test
    @DisplayName("findById: existing ID should return mapped DTO wrapped in Optional")
    void findById_found() {
        when(trainingDao.findById(10L)).thenReturn(Optional.of(training));
        when(trainingMapper.toDTO(training)).thenReturn(trainingDTO);

        Optional<TrainingDTO> result = service.findById(10L);

        assertTrue(result.isPresent());
        assertEquals(10L, result.get().getId());
        assertEquals(FITNESS, result.get().getType());
        verify(trainingMapper).toDTO(training);
    }

    @Test
    @DisplayName("findById: non-existent ID should return empty Optional")
    void findById_notFound() {
        when(trainingDao.findById(99L)).thenReturn(Optional.empty());

        Optional<TrainingDTO> result = service.findById(99L);

        assertTrue(result.isEmpty());
        verify(trainingMapper, never()).toDTO(any());
    }

    @Test
    @DisplayName("findAll: should return list of all mapped DTOs")
    void findAll_returnsList() {
        Training second = new Training();
        second.setId(11L);
        second.setTrainee(trainee);
        second.setTrainer(trainer);
        second.setName("Evening Yoga");
        second.setType(FITNESS);

        TrainingDTO secondDTO = new TrainingDTO(11L, 1L, 2L, "Evening Yoga", FITNESS, null, null);

        when(trainingDao.findAll()).thenReturn(List.of(training, second));
        when(trainingMapper.toDTO(training)).thenReturn(trainingDTO);
        when(trainingMapper.toDTO(second)).thenReturn(secondDTO);

        List<TrainingDTO> result = service.findAll();

        assertEquals(2, result.size());
        assertEquals("Morning Run", result.get(0).getName());
        assertEquals("Evening Yoga", result.get(1).getName());
        verify(trainingMapper, times(2)).toDTO(any());
    }

    @Test
    @DisplayName("findAll: empty storage should return empty list")
    void findAll_empty() {
        when(trainingDao.findAll()).thenReturn(List.of());

        List<TrainingDTO> result = service.findAll();

        assertTrue(result.isEmpty());
        verifyNoInteractions(trainingMapper);
    }
}

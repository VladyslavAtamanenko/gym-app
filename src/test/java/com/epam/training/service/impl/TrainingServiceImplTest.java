package com.epam.training.service.impl;

import com.epam.training.dao.TraineeDao;
import com.epam.training.dao.TrainerDao;
import com.epam.training.dao.TrainingDao;
import com.epam.training.dto.*;
import com.epam.training.mapper.ToDTOMapper;
import com.epam.training.mapper.ToEntityMapper;
import com.epam.training.model.Trainee;
import com.epam.training.model.Trainer;
import com.epam.training.model.Training;
import com.epam.training.model.TrainingType;
import com.epam.training.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TrainingService")
class TrainingServiceImplTest {

    @Mock private TrainingDao trainingDao;
    @Mock private TraineeDao traineeDao;
    @Mock private TrainerDao trainerDao;
    @Mock private ToEntityMapper<TrainingCreateRequest, Training> trainingCreateRequestMapper;
    @Mock private ToDTOMapper<Training, GetTrainingsByTraineeResponse> byTraineeResponseMapper;
    @Mock private ToDTOMapper<Training, GetTrainingsByTrainerResponse> byTrainerResponseMapper;

    @InjectMocks
    private TrainingServiceImpl trainingService;

    private Trainer trainer;
    private Trainee trainee;
    private Training training;
    private TrainingType yoga;

    @BeforeEach
    void setUp() {
        trainingService.setTrainingCreateRequestMapper(trainingCreateRequestMapper);
        trainingService.setByTraineeResponseMapper(byTraineeResponseMapper);
        trainingService.setByTrainerResponseMapper(byTrainerResponseMapper);

        yoga = TrainingType.builder().id(1L).name("Yoga").build();
        trainer = Trainer.builder()
                .id(1L)
                .user(User.builder().username("Trainer.One").build())
                .specialization(yoga)
                .build();
        trainee = Trainee.builder()
                .id(2L)
                .user(User.builder().username("Trainee.One").build())
                .build();
        training = Training.builder().id(3L).name("Morning Yoga").build();
    }

    @Test
    @DisplayName("create: saves training when trainer specialization matches requested type")
    void create_savesTrainingWhenTypeMatchesSpecialization() {
        TrainingCreateRequest request = new TrainingCreateRequest(
                "Trainee.One", "Trainer.One", "Morning Yoga", "Yoga",
                LocalDate.of(2024, 6, 1), 60);

        when(trainerDao.findByUsername("Trainer.One")).thenReturn(Optional.of(trainer));
        when(traineeDao.findByUsername("Trainee.One")).thenReturn(Optional.of(trainee));
        when(trainingCreateRequestMapper.toEntity(request)).thenReturn(training);
        when(trainingDao.save(training)).thenReturn(training);

        assertTrue(trainingService.create(request));

        assertEquals(trainee, training.getTrainee());
        assertEquals(trainer, training.getTrainer());
        assertEquals(yoga, training.getType());
    }

    @Test
    @DisplayName("create: throws IllegalArgumentException when training type does not match trainer specialization")
    void create_rejectsMismatchedTrainingType() {
        TrainingCreateRequest request = new TrainingCreateRequest(
                "Trainee.One", "Trainer.One", "Morning Yoga", "Cardio",
                LocalDate.of(2024, 6, 1), 60);
        when(trainerDao.findByUsername("Trainer.One")).thenReturn(Optional.of(trainer));

        assertThrows(IllegalArgumentException.class, () -> trainingService.create(request));
    }

    @Test
    @DisplayName("create: throws IllegalArgumentException when required fields are missing")
    void create_rejectsMissingRequiredFields() {
        TrainingCreateRequest request = new TrainingCreateRequest(
                "", "Trainer.One", "Morning Yoga", "Yoga",
                LocalDate.of(2024, 6, 1), 60);

        assertThrows(IllegalArgumentException.class, () -> trainingService.create(request));
    }

    @Test
    @DisplayName("create: throws IllegalArgumentException when duration is zero or negative")
    void create_rejectsNonPositiveDuration() {
        TrainingCreateRequest request = new TrainingCreateRequest(
                "Trainee.One", "Trainer.One", "Morning Yoga", "Yoga",
                LocalDate.of(2024, 6, 1), 0);

        assertThrows(IllegalArgumentException.class, () -> trainingService.create(request));
    }

    @Test
    @DisplayName("create: throws IllegalArgumentException when request is null")
    void create_rejectsNullRequest() {
        assertThrows(IllegalArgumentException.class, () -> trainingService.create(null));
    }

    @Test
    @DisplayName("create: throws NoSuchElementException when trainer is not found")
    void create_throwsWhenTrainerNotFound() {
        TrainingCreateRequest request = new TrainingCreateRequest(
                "Trainee.One", "missing", "Morning Yoga", "Yoga",
                LocalDate.of(2024, 6, 1), 60);
        when(trainerDao.findByUsername("missing")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> trainingService.create(request));
    }

    @Test
    @DisplayName("create: throws NoSuchElementException when trainee is not found")
    void create_throwsWhenTraineeNotFound() {
        TrainingCreateRequest request = new TrainingCreateRequest(
                "missing", "Trainer.One", "Morning Yoga", "Yoga",
                LocalDate.of(2024, 6, 1), 60);
        when(trainerDao.findByUsername("Trainer.One")).thenReturn(Optional.of(trainer));
        when(traineeDao.findByUsername("missing")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> trainingService.create(request));
    }

    @Test
    @DisplayName("findByTrainee: returns mapped trainings filtered by criteria")
    void findByTrainee_returnsMappedResults() {
        GetTrainingsByTraineeRequest request = new GetTrainingsByTraineeRequest(
                "Trainee.One", LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31), "Trainer.One", "Yoga");
        GetTrainingsByTraineeResponse response = new GetTrainingsByTraineeResponse();

        when(trainingDao.findByTrainee("Trainee.One", "Yoga",
                LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31), "Trainer.One"))
                .thenReturn(List.of(training));
        when(byTraineeResponseMapper.toDTO(training)).thenReturn(response);

        assertEquals(List.of(response), trainingService.findByTrainee(request));
    }

    @Test
    @DisplayName("findByTrainer: returns mapped trainings filtered by criteria")
    void findByTrainer_returnsMappedResults() {
        GetTrainingsByTrainerRequest request = new GetTrainingsByTrainerRequest(
                "Trainer.One", LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31), "Trainee.One");
        GetTrainingsByTrainerResponse response = new GetTrainingsByTrainerResponse();

        when(trainingDao.findByTrainer("Trainer.One",
                LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31), "Trainee.One"))
                .thenReturn(List.of(training));
        when(byTrainerResponseMapper.toDTO(training)).thenReturn(response);

        assertEquals(List.of(response), trainingService.findByTrainer(request));
    }

    @Test
    @DisplayName("findByTrainee: throws IllegalArgumentException when request is null")
    void findByTrainee_rejectsNullRequest() {
        assertThrows(IllegalArgumentException.class, () -> trainingService.findByTrainee(null));
    }

    @Test
    @DisplayName("findByTrainer: throws IllegalArgumentException when username is blank")
    void findByTrainer_rejectsBlankUsername() {
        GetTrainingsByTrainerRequest request = new GetTrainingsByTrainerRequest(
                "", null, null, null);
        assertThrows(IllegalArgumentException.class, () -> trainingService.findByTrainer(request));
    }

    @Test
    @DisplayName("findByTrainer: throws IllegalArgumentException when request is null")
    void findByTrainer_rejectsNullRequest() {
        assertThrows(IllegalArgumentException.class, () -> trainingService.findByTrainer(null));
    }
}

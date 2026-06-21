package com.epam.training.dao;

import com.epam.training.config.DaoTestAppConfig;
import com.epam.training.model.Trainee;
import com.epam.training.model.Trainer;
import com.epam.training.model.Training;
import com.epam.training.model.TrainingType;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(DaoTestAppConfig.class)
@Transactional
@DisplayName("TrainingDao")
class TrainingDaoTest {

    @Autowired
    private TrainingDao trainingDao;

    @Autowired
    private TraineeDao traineeDao;

    @Autowired
    private TrainerDao trainerDao;

    @Autowired
    private TrainingTypeDao trainingTypeDao;

    private Trainee trainee;
    private Trainer yogaTrainer;
    private Trainer fitnessTrainer;
    private TrainingType yoga;
    private TrainingType fitness;

    @BeforeEach
    void setUp() {
        yoga = trainingTypeDao.findByName("Yoga");
        fitness = trainingTypeDao.findByName("Fitness");

        trainee = traineeDao.save(DaoTestSupport.trainee(
                "trainee.one", LocalDate.of(1990, 3, 15), "Park Avenue"));
        yogaTrainer = trainerDao.save(DaoTestSupport.trainer("trainer.yoga", yoga));
        fitnessTrainer = trainerDao.save(DaoTestSupport.trainer("trainer.fitness", fitness));

        trainingDao.save(DaoTestSupport.training(
                "Morning Yoga", LocalDate.of(2024, 6, 1), 60, trainee, yogaTrainer, yoga));
        trainingDao.save(DaoTestSupport.training(
                "Evening Yoga", LocalDate.of(2024, 7, 15), 45, trainee, yogaTrainer, yoga));
        trainingDao.save(DaoTestSupport.training(
                "Fitness Blast", LocalDate.of(2024, 8, 1), 30, trainee, fitnessTrainer, fitness));
    }

    @Test
    @DisplayName("Save persists training with LocalDate")
    void save_persistsTrainingDate() {
        Training saved = trainingDao.save(DaoTestSupport.training(
                "New Session", LocalDate.of(2025, 1, 10), 90, trainee, yogaTrainer, yoga));

        assertNotNull(saved.getId());
        assertEquals(LocalDate.of(2025, 1, 10), saved.getDate());
    }

    @Test
    @DisplayName("FindByTrainee filters by date range")
    void findByTrainee_filtersByDateRange() {
        Pageable all = PageRequest.of(0, Integer.MAX_VALUE);
        List<Training> trainings = trainingDao.findByTrainee(
                "trainee.one", null,
                LocalDate.of(2024, 6, 1), LocalDate.of(2024, 7, 31), null, all).getContent();

        assertEquals(2, trainings.size());
        assertTrue(trainings.stream().allMatch(t ->
                !t.getDate().isBefore(LocalDate.of(2024, 6, 1))
                        && !t.getDate().isAfter(LocalDate.of(2024, 7, 31))));
    }

    @Test
    @DisplayName("FindByTrainee filters by training type")
    void findByTrainee_filtersByTrainingType() {
        Pageable all = PageRequest.of(0, Integer.MAX_VALUE);
        List<Training> trainings = trainingDao.findByTrainee(
                "trainee.one", "Fitness", null, null, null, all).getContent();

        assertEquals(1, trainings.size());
        assertEquals("Fitness Blast", trainings.get(0).getName());
    }

    @Test
    @DisplayName("FindByTrainee filters by trainer username")
    void findByTrainee_filtersByTrainer() {
        Pageable all = PageRequest.of(0, Integer.MAX_VALUE);
        List<Training> trainings = trainingDao.findByTrainee(
                "trainee.one", null, null, null, "trainer.yoga", all).getContent();

        assertEquals(2, trainings.size());
    }

    @Test
    @DisplayName("FindByTrainer filters by date range and trainee")
    void findByTrainer_filtersByDateRangeAndTrainee() {
        Pageable all = PageRequest.of(0, Integer.MAX_VALUE);
        List<Training> trainings = trainingDao.findByTrainer(
                "trainer.yoga",
                LocalDate.of(2024, 6, 1),
                LocalDate.of(2024, 6, 30),
                "trainee.one", all).getContent();

        assertEquals(1, trainings.size());
        assertEquals("Morning Yoga", trainings.get(0).getName());
        assertEquals(LocalDate.of(2024, 6, 1), trainings.get(0).getDate());
    }

    @Test
    @DisplayName("FindByTrainer returns all trainings when optional filters are null")
    void findByTrainer_returnsAllForTrainer() {
        Pageable all = PageRequest.of(0, Integer.MAX_VALUE);
        List<Training> trainings = trainingDao.findByTrainer("trainer.yoga", null, null, null, all).getContent();

        assertEquals(2, trainings.size());
    }

    @Test
    @DisplayName("Save null training throws exception")
    void save_nullTrainingThrows() {
        assertThrows(IllegalArgumentException.class, () -> trainingDao.save(null));
    }
}

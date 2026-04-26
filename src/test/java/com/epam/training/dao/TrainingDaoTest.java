package com.epam.training.dao;


import com.epam.training.config.AppConfig;
import com.epam.training.config.StorageConfig;
import com.epam.training.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig({AppConfig.class, StorageConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class TrainingDaoTest {

    private static final TrainingType FITNESS = new TrainingType(1L, "Fitness");
    private static final TrainingType YOGA = new TrainingType(2L, "Yoga");

    @Autowired
    private TrainingDao dao;

    private Training training;
    private Trainee trainee;
    private Trainer trainer;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUserName("jdoe");
        user.setPassword("pass");
        user.setIsActive(true);

        trainee = new Trainee();
        trainee.setId(1L);
        trainee.setUser(user);

        trainer = new Trainer();
        trainer.setId(1L);
        trainer.setUserInfo(user);
        trainer.setSpecialization(FITNESS);

        training = new Training();
        training.setId(1L);
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setName("Morning Workout");
        training.setType(FITNESS);
        training.setDate(LocalDateTime.now());
        training.setDuration(Duration.ofHours(1));
    }

    @Test
    @DisplayName("Save and FindById should persist and retrieve training")
    void testSaveAndFindById() {
        dao.save(training);

        Optional<Training> found = dao.findById(1L);

        assertTrue(found.isPresent());
        assertEquals("Morning Workout", found.get().getName());
        assertEquals(FITNESS, found.get().getType());
    }

    @Test
    @DisplayName("FindAll should return all trainings")
    void testFindAll() {
        dao.save(training);

        Training training2 = new Training();
        training2.setId(2L);
        training2.setTrainee(trainee);
        training2.setTrainer(trainer);
        training2.setName("Evening Yoga");
        training2.setType(YOGA);
        training2.setDate(LocalDateTime.now());
        training2.setDuration(Duration.ofMinutes(45));

        dao.save(training2);

        List<Training> all = dao.findAll();

        assertEquals(2, all.size());
    }

    @Test
    @DisplayName("Save null training should throw exception")
    void testSaveNullTrainingThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> dao.save(null));
    }

    @Test
    @DisplayName("Save training with null ID should throw exception")
    void testSaveTrainingWithNullIdThrowsException() {
        Training t = new Training();
        t.setId(null);

        assertThrows(IllegalArgumentException.class, () -> dao.save(t));
    }

    @Test
    @DisplayName("Save duplicate ID should throw exception")
    void testSaveDuplicateIdThrowsException() {
        dao.save(training);

        Training duplicate = new Training();
        duplicate.setId(1L);

        assertThrows(IllegalArgumentException.class, () -> dao.save(duplicate));
    }

    @Test
    @DisplayName("FindById for non-existent ID should return empty Optional")
    void testFindByIdNotFoundReturnsEmpty() {
        assertTrue(dao.findById(999L).isEmpty());
    }

    @Test
    @DisplayName("FindAll on empty DAO should return empty list")
    void testFindAllEmptyReturnsEmptyList() {
        assertTrue(dao.findAll().isEmpty());
    }
}

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

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(AppConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class TrainingDaoTest {

    private static final int SEEDED_RECORDS_COUNT = 10;

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
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUserName("jdoe");
        user.setPassword("pass");
        user.setIsActive(true);

        trainee = new Trainee();
        trainee.setUser(user);

        trainer = new Trainer();
        trainer.setUser(user);
        trainer.setSpecialization(FITNESS);

        training = new Training();
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setName("Morning Workout");
        training.setType(FITNESS);
        training.setDate(LocalDateTime.now());
        training.setDuration(Duration.ofHours(1));
    }

    @Test
    @DisplayName("Save with null ID should generate ID and persist")
    void testSaveGeneratesId() {
        Training saved = dao.save(training);

        assertNotNull(saved.getId());
        assertTrue(dao.findById(saved.getId()).isPresent());
    }

    @Test
    @DisplayName("Save and FindById should persist full training object")
    void testSaveAndFindById() {
        Training saved = dao.save(training);

        Training found = dao.findById(saved.getId()).orElseThrow();

        assertEquals("Morning Workout", found.getName());
        assertEquals(FITNESS, found.getType());
        assertEquals(FITNESS, found.getTrainer().getSpecialization());
    }

    @Test
    @DisplayName("Save with existing ID should update training")
    void testSaveUpdatesExisting() {
        Training saved = dao.save(training);

        saved.setName("Updated Workout");
        saved.setType(YOGA);

        dao.save(saved);

        Training updated = dao.findById(saved.getId()).orElseThrow();

        assertEquals("Updated Workout", updated.getName());
        assertEquals(YOGA, updated.getType());
    }

    @Test
    @DisplayName("FindAll should return all trainings")
    void testFindAll() {
        dao.save(training);

        Training t2 = new Training();
        t2.setTrainee(trainee);
        t2.setTrainer(trainer);
        t2.setName("Evening Yoga");
        t2.setType(YOGA);
        t2.setDate(LocalDateTime.now());
        t2.setDuration(Duration.ofMinutes(45));

        dao.save(t2);

        List<Training> all = dao.findAll();

        assertEquals(SEEDED_RECORDS_COUNT + 2, all.size());
    }

    @Test
    @DisplayName("Delete should remove trainee")
    void testDelete() {
        Training saved = dao.save(training);

        dao.delete(saved.getId());

        assertTrue(dao.findById(saved.getId()).isEmpty());
        assertEquals(SEEDED_RECORDS_COUNT, dao.findAll().size());
    }

    @Test
    @DisplayName("Save null training should throw exception")
    void testSaveNullThrows() {
        assertThrows(IllegalArgumentException.class, () -> dao.save(null));
    }

    @Test
    @DisplayName("FindById for non-existent ID should return empty")
    void testFindByIdNotFound() {
        assertTrue(dao.findById(999L).isEmpty());
    }

    @Test
    @DisplayName("Delete non-existent ID should not throw")
    void testDeleteNonExistent() {
        assertDoesNotThrow(() -> dao.delete(999L));
    }

    @Test
    @DisplayName("FindAll should return seeded trainings")
    void testFindAllSeeded() {
        assertEquals(SEEDED_RECORDS_COUNT, dao.findAll().size());
    }
}

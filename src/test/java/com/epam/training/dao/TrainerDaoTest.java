package com.epam.training.dao;


import com.epam.training.config.AppConfig;
import com.epam.training.config.StorageConfig;
import com.epam.training.model.Trainer;
import com.epam.training.model.TrainingType;
import com.epam.training.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig({AppConfig.class, StorageConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class TrainerDaoTest {

    private static final TrainingType FITNESS = new TrainingType(1L, "Fitness");
    private static final TrainingType YOGA = new TrainingType(2L, "Yoga");

    @Autowired
    private TrainerDao dao;

    private Trainer trainer;
    private User user;

    @BeforeEach
    void setUp() {


        user = new User();
        user.setId(1L);
        user.setFirstName("Alice");
        user.setLastName("Smith");
        user.setUserName("asmith");
        user.setPassword("pass");
        user.setIsActive(true);

        trainer = new Trainer();
        trainer.setId(1L);
        trainer.setSpecialization(FITNESS);
        trainer.setUserInfo(user);
    }


    @Test
    @DisplayName("Save and FindById should persist and retrieve trainer")
    void testSaveAndFindById() {
        dao.save(trainer);
        Optional<Trainer> found = dao.findById(1L);
        assertTrue(found.isPresent());
        assertEquals(FITNESS, found.get().getSpecialization());
    }

    @Test
    @DisplayName("FindAll should return all trainers")
    void testFindAll() {
        dao.save(trainer);
        Trainer trainer2 = new Trainer();
        trainer2.setId(2L);
        trainer2.setSpecialization(YOGA);
        trainer2.setUserInfo(user);
        dao.save(trainer2);

        List<Trainer> all = dao.findAll();
        assertEquals(2, all.size());
    }

    @Test
    @DisplayName("Update should modify trainer details")
    void testUpdate() {
        dao.save(trainer);
        trainer.setSpecialization(YOGA);
        dao.update(trainer);
        Optional<Trainer> updated = dao.findById(1L);
        assertTrue(updated.isPresent());
        assertEquals(YOGA, updated.get().getSpecialization());
    }

    @Test
    @DisplayName("Save null trainer should throw exception")
    void testSaveNullTrainerThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> dao.save(null));
    }

    @Test
    @DisplayName("Save trainer with null ID should throw exception")
    void testSaveTrainerWithNullIdThrowsException() {
        Trainer trainer = new Trainer();
        trainer.setId(null);
        assertThrows(IllegalArgumentException.class, () -> dao.save(trainer));
    }

    @Test
    @DisplayName("Save duplicate ID should throw exception")
    void testSaveDuplicateIdThrowsException() {
        Trainer trainer1 = new Trainer();
        trainer1.setId(1L);
        dao.save(trainer1);
        Trainer trainer2 = new Trainer();
        trainer2.setId(1L);
        assertThrows(IllegalArgumentException.class, () -> dao.save(trainer2));
    }

    @Test
    @DisplayName("FindById for non-existent ID should return empty Optional")
    void testFindByIdNotFoundReturnsEmpty() {
        assertTrue(dao.findById(999L).isEmpty());
    }

    @Test
    @DisplayName("Update non-existent trainer should throw exception")
    void testUpdateNonExistentTrainerThrowsException() {
        Trainer trainer = new Trainer();
        trainer.setId(1L);
        Assertions.assertThrows(NoSuchElementException.class, () -> dao.update(trainer));
    }

    @Test
    @DisplayName("Update null trainer should throw exception")
    void testUpdateNullTrainerThrowsException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> dao.update(null));
    }

    @Test
    @DisplayName("Update trainer with null ID should throw exception")
    void testUpdateTrainerWithNullIdThrowsException() {
        Trainer trainer = new Trainer();
        trainer.setId(null);
        Assertions.assertThrows(IllegalArgumentException.class, () -> dao.update(trainer));
    }

    @Test
    @DisplayName("FindAll on empty DAO should return empty list")
    void testFindAllEmptyReturnsEmptyList() {
        Assertions.assertTrue(dao.findAll().isEmpty());
    }

}

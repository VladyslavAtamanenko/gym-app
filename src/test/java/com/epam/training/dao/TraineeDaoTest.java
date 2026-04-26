package com.epam.training.dao;

import com.epam.training.config.AppConfig;
import com.epam.training.config.StorageConfig;
import com.epam.training.model.Trainee;
import com.epam.training.model.User;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;


import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig({AppConfig.class, StorageConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class TraineeDaoTest {

    @Autowired
    private TraineeDao dao;

    private Trainee trainee;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUserName("jdoe");
        user.setPassword("pass");
        user.setIsActive(true);

        trainee = new Trainee();
        trainee.setId(1L);
        trainee.setDateOfBirth(LocalDate.of(1990, 1, 1));
        trainee.setAddress("123 Main St");
        trainee.setUser(user);
    }


        @Test
        @DisplayName("Save and FindById should persist and retrieve trainee")
        void testSaveAndFindById() {
            dao.save(trainee);
            Optional<Trainee> found = dao.findById(1L);
            assertTrue(found.isPresent());
            assertEquals("123 Main St", found.get().getAddress());
        }

        @Test
        @DisplayName("FindAll should return all trainees")
        void testFindAll() {
            dao.save(trainee);
            Trainee trainee2 = new Trainee();
            trainee2.setId(2L);
            trainee2.setDateOfBirth(LocalDate.of(1992, 2, 2));
            trainee2.setAddress("456 Elm St");
            trainee2.setUser(user);
            dao.save(trainee2);

            List<Trainee> all = dao.findAll();
            assertEquals(2, all.size());
        }

        @Test
        @DisplayName("Update should modify trainee details")
        void testUpdate() {
            dao.save(trainee);
            trainee.setAddress("New Address");
            dao.update(trainee);
            Optional<Trainee> updated = dao.findById(1L);
            assertTrue(updated.isPresent());
            assertEquals("New Address", updated.get().getAddress());
        }

        @Test
        @DisplayName("Delete should remove trainee")
        void testDelete() {
            dao.save(trainee);
            dao.delete(1L);
            assertFalse(dao.findById(1L).isPresent());
            assertTrue(dao.findAll().isEmpty());
        }

        @Test
        @DisplayName("Save null trainee should throw exception")
        void testSaveNullTraineeThrowsException() {
            assertThrows(IllegalArgumentException.class, () -> dao.save(null));
        }

        @Test
        @DisplayName("Save trainee with null ID should throw exception")
        void testSaveTraineeWithNullIdThrowsException() {
            Trainee trainee = new Trainee();
            trainee.setId(null);
            assertThrows(IllegalArgumentException.class, () -> dao.save(trainee));
        }

        @Test
        @DisplayName("Save duplicate ID should throw exception")
        void testSaveDuplicateIdThrowsException() {
            Trainee trainee1 = new Trainee();
            trainee1.setId(1L);
            dao.save(trainee1);

            Trainee trainee2 = new Trainee();
            trainee2.setId(1L);
            assertThrows(IllegalArgumentException.class, () -> dao.save(trainee2));
        }

        @Test
        @DisplayName("FindById for non-existent ID should return empty optional")
        void testFindByIdNotFoundReturnsEmptyOptional() {
            assertTrue(dao.findById(999L).isEmpty());
        }

        @Test
        @DisplayName("Update non-existent trainee should throw exception")
        void testUpdateNonExistentTraineeThrowsException() {
            Trainee trainee = new Trainee();
            trainee.setId(1L);
            assertThrows(NoSuchElementException.class, () -> dao.update(trainee));
        }

        @Test
        @DisplayName("Update null trainee should throw exception")
        void testUpdateNullTraineeThrowsException() {
            assertThrows(IllegalArgumentException.class, () -> dao.update(null));
        }

        @Test
        @DisplayName("Update trainee with null ID should throw exception")
        void testUpdateTraineeWithNullIdThrowsException() {
            Trainee trainee = new Trainee();
            trainee.setId(null);
            assertThrows(IllegalArgumentException.class, () -> dao.update(trainee));
        }

        @Test
        @DisplayName("Delete non-existent ID should not throw exception")
        void testDeleteNonExistentIdDoesNotThrow() {
            assertDoesNotThrow(() -> dao.delete(999L));
        }

        @Test
        @DisplayName("FindAll on empty DAO should return empty list")
        void testFindAllEmptyReturnsEmptyList() {
            assertTrue(dao.findAll().isEmpty());
        }
    }


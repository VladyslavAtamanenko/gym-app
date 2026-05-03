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
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUserName("jdoe");
        user.setPassword("pass");
        user.setIsActive(true);

        trainee = new Trainee();
        trainee.setDateOfBirth(LocalDate.of(1990, 1, 1));
        trainee.setAddress("123 Main St");
        trainee.setUser(user);
    }

    @Test
    @DisplayName("Save with null ID should generate ID and persist")
    void testSaveGeneratesId() {
        Trainee saved = dao.save(trainee);

        assertNotNull(saved.getId());
        assertTrue(dao.findById(saved.getId()).isPresent());
    }

    @Test
    @DisplayName("Save with existing ID should update trainee")
    void testSaveUpdatesExisting() {
        Trainee saved = dao.save(trainee);

        saved.setAddress("New Address");
        dao.save(saved);

        Optional<Trainee> updated = dao.findById(saved.getId());
        assertTrue(updated.isPresent());
        assertEquals("New Address", updated.get().getAddress());
    }


    @Test
    @DisplayName("FindAll should return all trainees")
    void testFindAll() {
        Trainee t1 = dao.save(trainee);

        Trainee t2 = new Trainee();
        t2.setDateOfBirth(LocalDate.of(1992, 2, 2));
        t2.setAddress("456 Elm St");
        t2.setUser(user);

        dao.save(t2);

        List<Trainee> all = dao.findAll();
        assertEquals(2, all.size());
    }

    @Test
    @DisplayName("Delete should remove trainee")
    void testDelete() {
        Trainee saved = dao.save(trainee);

        dao.delete(saved.getId());

        assertTrue(dao.findById(saved.getId()).isEmpty());
        assertTrue(dao.findAll().isEmpty());
    }

    @Test
    @DisplayName("Save null trainee should throw exception")
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
    @DisplayName("FindAll on empty DAO should return empty list")
    void testFindAllEmpty() {
        assertTrue(dao.findAll().isEmpty());
    }
}


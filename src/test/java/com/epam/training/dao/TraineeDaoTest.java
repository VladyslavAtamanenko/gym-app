package com.epam.training.dao;

import com.epam.training.config.AppConfig;
import com.epam.training.config.PersistenceConfig;
import com.epam.training.model.*;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig({AppConfig.class, PersistenceConfig.class})
@Transactional
class TraineeDaoTest {

    @Autowired
    private TraineeDao dao;

    private Trainee trainee;

    @BeforeEach
    void setUp() {

        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUsername("jdoe");
        user.setPassword("pass");
        user.setIsActive(true);

        trainee = new Trainee();
        trainee.setDateOfBirth(LocalDate.of(1990, 1, 1));
        trainee.setAddress("123 Main St");
        trainee.setUser(user);
    }

    @Test
    @DisplayName("Save should persist trainee")
    void testSave() {

        Trainee saved = dao.save(trainee);

        assertNotNull(saved.getId());

        Optional<Trainee> found =
                dao.findByUsername("jdoe");

        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
    }

    @Test
    @DisplayName("Save existing trainee should update data")
    void testUpdate() {

        dao.save(trainee);

        trainee.setAddress("New Address");

        dao.save(trainee);

        Trainee updated = dao.findByUsername("jdoe")
                .orElseThrow();

        assertEquals("New Address",
                updated.getAddress());
    }

    @Test
    @DisplayName("FindByUsername should return trainee")
    void testFindByUsername() {

        dao.save(trainee);

        Optional<Trainee> result =
                dao.findByUsername("jdoe");

        assertTrue(result.isPresent());
        assertEquals("John",
                result.get().getUser().getFirstName());
    }

    @Test
    @DisplayName("FindByUsername should return empty for unknown username")
    void testFindByUsernameNotFound() {

        assertTrue(
                dao.findByUsername("unknown")
                        .isEmpty()
        );
    }

    @Test
    @DisplayName("FindAll should return saved trainees")
    void testFindAll() {

        dao.save(trainee);

        User secondUser = new User();
        secondUser.setFirstName("Jane");
        secondUser.setLastName("Smith");
        secondUser.setUsername("jsmith");
        secondUser.setPassword("pass");
        secondUser.setIsActive(true);

        Trainee second = new Trainee();
        second.setDateOfBirth(LocalDate.of(1992, 2, 2));
        second.setAddress("456 Elm St");
        second.setUser(secondUser);

        dao.save(second);

        List<Trainee> trainees = dao.findAll();

        assertEquals(2, trainees.size());
    }

    @Test
    @DisplayName("Delete should remove trainee")
    void testDelete() {

        dao.save(trainee);

        dao.delete("jdoe");

        assertTrue(
                dao.findByUsername("jdoe")
                        .isEmpty()
        );
    }

    @Test
    @DisplayName("Delete non-existing username should not throw")
    void testDeleteNonExisting() {

        assertDoesNotThrow(
                () -> dao.delete("unknown")
        );
    }

    @Test
    @DisplayName("Save null trainee should throw exception")
    void testSaveNull() {

        assertThrows(
                IllegalArgumentException.class,
                () -> dao.save(null)
        );
    }
}
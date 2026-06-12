package com.epam.training.dao;

import com.epam.training.config.DaoTestAppConfig;
import com.epam.training.model.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(DaoTestAppConfig.class)
@Transactional
@DisplayName("TraineeDao")
class TraineeDaoTest {

    @Autowired
    private TraineeDao dao;

    @Autowired
    private TrainerDao trainerDao;

    @Autowired
    private TrainingDao trainingDao;

    @Autowired
    private TrainingTypeDao trainingTypeDao;

    @PersistenceContext
    private EntityManager entityManager;

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
    @DisplayName("save: persists trainee and assigns an id")
    void testSave() {
        Trainee saved = dao.save(trainee);

        assertNotNull(saved.getId());

        Optional<Trainee> found = dao.findByUsername("jdoe");

        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
        assertEquals(LocalDate.of(1990, 1, 1), found.get().getDateOfBirth());
    }

    @Test
    @DisplayName("save: updates data when called on an already-persisted trainee")
    void testUpdate() {
        dao.save(trainee);

        trainee.setAddress("New Address");

        dao.save(trainee);

        Trainee updated = dao.findByUsername("jdoe").orElseThrow();

        assertEquals("New Address", updated.getAddress());
    }

    @Test
    @DisplayName("findByUsername: returns trainee when username matches")
    void testFindByUsername() {
        dao.save(trainee);

        Optional<Trainee> result = dao.findByUsername("jdoe");

        assertTrue(result.isPresent());
        assertEquals("John", result.get().getUser().getFirstName());
    }

    @Test
    @DisplayName("findByUsername: returns empty Optional for unknown username")
    void testFindByUsernameNotFound() {
        assertTrue(dao.findByUsername("unknown").isEmpty());
    }

    @Test
    @DisplayName("findAll: returns all saved trainees")
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
    @DisplayName("delete: removes the trainee from the database")
    void testDelete() {
        dao.save(trainee);

        dao.delete("jdoe");

        assertTrue(dao.findByUsername("jdoe").isEmpty());
    }

    @Test
    @DisplayName("delete: does not throw when username does not exist")
    void testDeleteNonExisting() {
        assertDoesNotThrow(() -> dao.delete("unknown"));
    }

    @Test
    @DisplayName("save: throws exception when trainee is null")
    void testSaveNull() {
        assertThrows(IllegalArgumentException.class, () -> dao.save(null));
    }

    @Test
    @DisplayName("delete: cascades to remove all trainings belonging to the deleted trainee")
    void delete_cascadesTrainingDeletion() {
        TrainingType yoga = trainingTypeDao.findByName("Yoga");

        Trainee savedTrainee = dao.save(trainee);
        Trainer savedTrainer = trainerDao.save(DaoTestSupport.trainer("trainer.cascade", yoga));

        trainingDao.save(DaoTestSupport.training(
                "Morning Yoga", LocalDate.of(2024, 6, 1), 60, savedTrainee, savedTrainer, yoga));
        trainingDao.save(DaoTestSupport.training(
                "Evening Yoga", LocalDate.of(2024, 6, 2), 45, savedTrainee, savedTrainer, yoga));

        entityManager.flush();
        entityManager.clear();

        long countBefore = entityManager
                .createQuery("SELECT COUNT(t) FROM Training t WHERE t.trainee.id = :id", Long.class)
                .setParameter("id", savedTrainee.getId())
                .getSingleResult();
        assertEquals(2, countBefore);

        entityManager.createQuery(
                "SELECT t FROM Trainee t LEFT JOIN FETCH t.trainings WHERE t.user.username = 'jdoe'",
                Trainee.class).getSingleResult();

        dao.delete("jdoe");
        entityManager.flush();

        long countAfter = entityManager
                .createQuery("SELECT COUNT(t) FROM Training t WHERE t.trainee.id = :id", Long.class)
                .setParameter("id", savedTrainee.getId())
                .getSingleResult();
        assertEquals(0, countAfter);
    }
}

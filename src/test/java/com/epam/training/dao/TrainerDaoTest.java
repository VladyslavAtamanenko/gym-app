package com.epam.training.dao;

import com.epam.training.config.DaoTestAppConfig;
import com.epam.training.model.Trainee;
import com.epam.training.model.Trainer;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(DaoTestAppConfig.class)
@Transactional
@DisplayName("TrainerDao")
class TrainerDaoTest {

    @Autowired
    private TrainerDao trainerDao;

    @Autowired
    private TraineeDao traineeDao;

    @Autowired
    private TrainingTypeDao trainingTypeDao;

    private TrainingType yoga;
    private TrainingType fitness;
    private Trainer assignedTrainer;
    private Trainer unassignedTrainer;

    @BeforeEach
    void setUp() {
        yoga = trainingTypeDao.findByName("Yoga");
        fitness = trainingTypeDao.findByName("Fitness");

        assignedTrainer = trainerDao.save(DaoTestSupport.trainer("trainer.assigned", yoga));
        unassignedTrainer = trainerDao.save(DaoTestSupport.trainer("trainer.free", fitness));

        Trainee trainee = traineeDao.save(DaoTestSupport.trainee(
                "trainee.one", LocalDate.of(1995, 5, 10), "Main street"));
        trainee.setTrainers(new ArrayList<>(List.of(assignedTrainer)));
        traineeDao.save(trainee);
    }

    @Test
    @DisplayName("Save should persist new trainer")
    void save_persistsTrainer() {
        Trainer saved = trainerDao.save(DaoTestSupport.trainer("trainer.new", yoga));

        assertNotNull(saved.getId());
        assertEquals("trainer.new", saved.getUser().getUsername());
    }

    @Test
    @DisplayName("Save should update existing trainer")
    void save_updatesTrainer() {
        assignedTrainer.getUser().setFirstName("Updated");
        trainerDao.save(assignedTrainer);

        Trainer found = trainerDao.findByUsername("trainer.assigned").orElseThrow();
        assertEquals("Updated", found.getUser().getFirstName());
    }

    @Test
    @DisplayName("FindByUsername returns trainer when present")
    void findByUsername_returnsTrainer() {
        Optional<Trainer> result = trainerDao.findByUsername("trainer.assigned");

        assertTrue(result.isPresent());
        assertEquals("Yoga", result.get().getSpecialization().getName());
    }

    @Test
    @DisplayName("FindByUsername returns empty for unknown username")
    void findByUsername_returnsEmpty() {
        assertTrue(trainerDao.findByUsername("unknown").isEmpty());
    }

    @Test
    @DisplayName("FindAll returns all trainers")
    void findAll_returnsAllTrainers() {
        assertEquals(2, trainerDao.findAll().size());
    }

    @Test
    @DisplayName("FindNotAssignedOnTrainee excludes trainers already linked to trainee")
    void findNotAssignedOnTrainee_excludesAssignedTrainers() {
        Pageable all = PageRequest.of(0, Integer.MAX_VALUE);
        List<Trainer> trainers = trainerDao.findNotAssignedOnTrainee("trainee.one", all).getContent();

        assertEquals(1, trainers.size());
        assertEquals("trainer.free", trainers.get(0).getUser().getUsername());
    }

    @Test
    @DisplayName("FindNotAssignedOnTrainee excludes inactive trainers")
    void findNotAssignedOnTrainee_excludesInactiveTrainers() {
        Pageable all = PageRequest.of(0, Integer.MAX_VALUE);
        unassignedTrainer.getUser().setIsActive(false);
        trainerDao.save(unassignedTrainer);

        List<Trainer> trainers = trainerDao.findNotAssignedOnTrainee("trainee.one", all).getContent();

        assertTrue(trainers.isEmpty());
    }

    @Test
    @DisplayName("Save null trainer throws exception")
    void save_nullTrainerThrows() {
        assertThrows(IllegalArgumentException.class, () -> trainerDao.save(null));
    }
}

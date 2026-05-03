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

    private static final int SEEDED_RECORDS_COUNT = 10;

    @Autowired
    private TrainerDao dao;

    private Trainer trainer;

    private static final TrainingType FITNESS = new TrainingType(1L, "Fitness");
    private static final TrainingType YOGA = new TrainingType(2L, "Yoga");

    @BeforeEach
    void setUp() {
        trainer = new Trainer();
        trainer.setSpecialization(FITNESS);
    }

    @Test
    @DisplayName("Save with null ID should generate ID")
    void testSaveGeneratesId() {
        Trainer saved = dao.save(trainer);

        assertNotNull(saved.getId());
        assertTrue(dao.findById(saved.getId()).isPresent());
    }

    @Test
    @DisplayName("Save should persist specialization")
    void testSavePersistsSpecialization() {
        Trainer saved = dao.save(trainer);

        Trainer found = dao.findById(saved.getId()).orElseThrow();

        assertEquals(FITNESS.getId(), found.getSpecialization().getId());
        assertEquals(FITNESS.getName(), found.getSpecialization().getName());
    }

    @Test
    @DisplayName("Save with existing ID should update specialization")
    void testSaveUpdatesExisting() {
        Trainer saved = dao.save(trainer);

        saved.setSpecialization(YOGA);
        dao.save(saved);

        Trainer updated = dao.findById(saved.getId()).orElseThrow();

        assertEquals(YOGA.getId(), updated.getSpecialization().getId());
        assertEquals(YOGA.getName(), updated.getSpecialization().getName());
    }


    @Test
    @DisplayName("FindById should return trainer if exists")
    void testFindById() {
        Trainer saved = dao.save(trainer);

        Optional<Trainer> found = dao.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals(FITNESS.getName(), found.get().getSpecialization().getName());
    }

    @Test
    @DisplayName("FindAll should return all trainers")
    void testFindAll() {
        dao.save(trainer);

        Trainer t2 = new Trainer();
        t2.setSpecialization(YOGA);

        dao.save(t2);

        List<Trainer> all = dao.findAll();
        assertEquals(SEEDED_RECORDS_COUNT + 2, all.size());
    }

    @Test
    @DisplayName("FindById for non-existent ID should return empty")
    void testFindByIdNotFound() {
        assertTrue(dao.findById(999L).isEmpty());
    }

    @Test
    @DisplayName("Save null trainer should throw exception")
    void testSaveNullThrows() {
        assertThrows(IllegalArgumentException.class, () -> dao.save(null));
    }

    @Test
    @DisplayName("FindAll should return seeded trainers")
    void testFindAllSeeded() {
        assertEquals(SEEDED_RECORDS_COUNT, dao.findAll().size());
    }
}

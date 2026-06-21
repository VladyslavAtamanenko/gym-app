package com.epam.training.dao;

import com.epam.training.config.DaoTestAppConfig;
import com.epam.training.model.TrainingType;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.epam.training.exception.TrainingTypeNotFoundException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(DaoTestAppConfig.class)
@Transactional
@DisplayName("TrainingTypeDao")
class TrainingTypeDaoTest {

    @Autowired
    private TrainingTypeDao trainingTypeDao;

    private TrainingType yoga;

    @BeforeEach
    void setUp() {
        yoga = trainingTypeDao.findByName("Yoga");
    }

    @Test
    @DisplayName("FindByName returns persisted training type")
    void findByName_returnsType() {
        TrainingType found = trainingTypeDao.findByName("Yoga");

        assertEquals("Yoga", found.getName());
        assertNotNull(found.getId());
    }

    @Test
    @DisplayName("FindByName throws when type is missing")
    void findByName_throwsWhenMissing() {
        assertThrows(TrainingTypeNotFoundException.class, () -> trainingTypeDao.findByName("Pilates"));
    }

    @Test
    @DisplayName("FindById returns persisted training type")
    void findById_returnsType() {
        TrainingType found = trainingTypeDao.findById(yoga.getId());

        assertEquals("Yoga", found.getName());
    }

    @Test
    @DisplayName("FindById throws when id is missing")
    void findById_throwsWhenMissing() {
        assertThrows(TrainingTypeNotFoundException.class, () -> trainingTypeDao.findById(999L));
    }

    @Test
    @DisplayName("FindAll returns all training types")
    void findAll_returnsAllTypes() {
        List<TrainingType> types = trainingTypeDao.findAll();

        assertEquals(5, types.size());
    }
}

package com.epam.training.health;

import com.epam.training.dao.TrainingTypeDao;
import com.epam.training.model.TrainingType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("TrainingTypeHealthIndicator")
class TrainingTypeHealthIndicatorTest {

    @Mock
    private TrainingTypeDao trainingTypeDao;

    @InjectMocks
    private TrainingTypeHealthIndicator indicator;

    @Test
    @DisplayName("UP when training types are present")
    void up_whenTrainingTypesExist() {
        when(trainingTypeDao.findAll()).thenReturn(List.of(
                new TrainingType(1L, "Yoga"),
                new TrainingType(2L, "Fitness")));

        Health health = indicator.health();

        assertEquals(Status.UP, health.getStatus());
        assertEquals(2, health.getDetails().get("trainingTypes"));
    }

    @Test
    @DisplayName("DOWN when no training types are found")
    void down_whenNoTrainingTypes() {
        when(trainingTypeDao.findAll()).thenReturn(List.of());

        Health health = indicator.health();

        assertEquals(Status.DOWN, health.getStatus());
        assertEquals(0, health.getDetails().get("trainingTypes"));
    }

    @Test
    @DisplayName("DOWN when DAO throws an exception")
    void down_whenDaoThrows() {
        when(trainingTypeDao.findAll()).thenThrow(new RuntimeException("DB unavailable"));

        Health health = indicator.health();

        assertEquals(Status.DOWN, health.getStatus());
    }
}

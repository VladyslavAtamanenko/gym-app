package com.epam.training.health;

import com.epam.training.metrics.GymActiveCountService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ActiveTrainersHealthIndicator")
class ActiveTrainersHealthIndicatorTest {

    @Mock
    private GymActiveCountService countService;

    @InjectMocks
    private ActiveTrainersHealthIndicator indicator;

    @Test
    @DisplayName("UP when at least one active trainer exists")
    void up_whenActiveTrainersExist() {
        when(countService.countActiveTrainers()).thenReturn(3L);

        Health health = indicator.health();

        assertEquals(Status.UP, health.getStatus());
        assertEquals(3L, health.getDetails().get("activeTrainers"));
    }

    @Test
    @DisplayName("OUT_OF_SERVICE when no active trainers")
    void outOfService_whenNoActiveTrainers() {
        when(countService.countActiveTrainers()).thenReturn(0L);

        Health health = indicator.health();

        assertEquals(Status.OUT_OF_SERVICE, health.getStatus());
        assertEquals(0, ((Number) health.getDetails().get("activeTrainers")).intValue());
    }

    @Test
    @DisplayName("DOWN when count service throws an exception")
    void down_whenServiceThrows() {
        when(countService.countActiveTrainers()).thenThrow(new RuntimeException("DB error"));

        Health health = indicator.health();

        assertEquals(Status.DOWN, health.getStatus());
    }
}

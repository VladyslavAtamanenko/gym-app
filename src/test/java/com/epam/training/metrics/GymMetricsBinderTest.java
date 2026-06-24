package com.epam.training.metrics;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GymMetricsBinder")
class GymMetricsBinderTest {

    @Mock
    private GymActiveCountService countService;

    private SimpleMeterRegistry registry;
    private GymMetricsBinder binder;

    @BeforeEach
    void setUp() {
        registry = new SimpleMeterRegistry();
        binder = new GymMetricsBinder(countService);
        binder.bindTo(registry);
    }

    @Test
    @DisplayName("registers gym.active.trainees gauge")
    void registersActiveTraineesGauge() {
        Gauge gauge = registry.find("gym.active.trainees").gauge();
        assertNotNull(gauge, "gym.active.trainees gauge should be registered");
    }

    @Test
    @DisplayName("registers gym.active.trainers gauge")
    void registersActiveTrainersGauge() {
        Gauge gauge = registry.find("gym.active.trainers").gauge();
        assertNotNull(gauge, "gym.active.trainers gauge should be registered");
    }

    @Test
    @DisplayName("gym.active.trainees gauge value reflects countService")
    void activeTraineesGaugeValue_reflectsService() {
        when(countService.countActiveTrainees()).thenReturn(5L);

        double value = registry.find("gym.active.trainees").gauge().value();

        assertEquals(5.0, value);
    }

    @Test
    @DisplayName("gym.active.trainers gauge value reflects countService")
    void activeTrainersGaugeValue_reflectsService() {
        when(countService.countActiveTrainers()).thenReturn(2L);

        double value = registry.find("gym.active.trainers").gauge().value();

        assertEquals(2.0, value);
    }
}

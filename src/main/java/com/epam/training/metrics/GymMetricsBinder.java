package com.epam.training.metrics;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GymMetricsBinder implements MeterBinder {

    private final GymActiveCountService countService;

    @Autowired
    public GymMetricsBinder(GymActiveCountService countService) {
        this.countService = countService;
    }

    @Override
    public void bindTo(MeterRegistry registry) {
        Gauge.builder("gym.active.trainees", countService, GymActiveCountService::countActiveTrainees)
                .description("Number of currently active trainees")
                .register(registry);

        Gauge.builder("gym.active.trainers", countService, GymActiveCountService::countActiveTrainers)
                .description("Number of currently active trainers")
                .register(registry);
    }
}

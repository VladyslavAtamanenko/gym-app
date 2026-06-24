package com.epam.training.health;

import com.epam.training.metrics.GymActiveCountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class ActiveTrainersHealthIndicator implements HealthIndicator {

    private final GymActiveCountService countService;

    @Autowired
    public ActiveTrainersHealthIndicator(GymActiveCountService countService) {
        this.countService = countService;
    }

    @Override
    public Health health() {
        try {
            long activeTrainers = countService.countActiveTrainers();
            if (activeTrainers > 0) {
                return Health.up()
                        .withDetail("activeTrainers", activeTrainers)
                        .build();
            }
            return Health.outOfService()
                    .withDetail("activeTrainers", 0)
                    .withDetail("message", "No active trainers — trainees cannot be assigned")
                    .build();
        } catch (Exception e) {
            return Health.down()
                    .withDetail("message", "Failed to count active trainers")
                    .withException(e)
                    .build();
        }
    }
}

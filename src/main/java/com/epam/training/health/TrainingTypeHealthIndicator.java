package com.epam.training.health;

import com.epam.training.dao.TrainingTypeDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class TrainingTypeHealthIndicator implements HealthIndicator {

    private final TrainingTypeDao trainingTypeDao;

    @Autowired
    public TrainingTypeHealthIndicator(TrainingTypeDao trainingTypeDao) {
        this.trainingTypeDao = trainingTypeDao;
    }

    @Override
    public Health health() {
        try {
            int count = trainingTypeDao.findAll().size();
            if (count > 0) {
                return Health.up()
                        .withDetail("trainingTypes", count)
                        .withDetail("message", "Reference data loaded")
                        .build();
            }
            return Health.down()
                    .withDetail("trainingTypes", 0)
                    .withDetail("message", "No training types found — training creation will fail")
                    .build();
        } catch (Exception e) {
            return Health.down()
                    .withDetail("message", "Failed to query training types")
                    .withException(e)
                    .build();
        }
    }
}

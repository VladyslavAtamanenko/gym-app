package com.epam.training.mapper;

import com.epam.training.dto.TrainingCreateRequest;
import com.epam.training.model.Training;
import com.epam.training.model.TrainingType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TrainingCreateRequestMapperTest {
    private final TrainingCreateRequestMapper mapper = new TrainingCreateRequestMapper();

    private static final TrainingType FITNESS = new TrainingType(1L, "Fitness");

    @Test
    @DisplayName("toEntity: maps all training fields including stub trainee/trainer by ID")
    void toEntity_mapsAllFields() {
        LocalDateTime date = LocalDateTime.of(2025, 9, 1, 9, 0);
        Duration duration  = Duration.ofMinutes(90);

        TrainingCreateRequest req = new TrainingCreateRequest(
                1L, 2L, "Power Session", FITNESS, date, duration);

        Training entity = mapper.toEntity(req);

        assertEquals("Power Session", entity.getName());
        assertEquals(FITNESS,         entity.getType());
        assertEquals(date,            entity.getDate());
        assertEquals(duration,        entity.getDuration());
        // stub objects carry only the IDs; full entities are resolved in the service
        assertEquals(1L, entity.getTrainee().getId());
        assertEquals(2L, entity.getTrainer().getId());
    }
}
package com.epam.training.mapper;

import com.epam.training.dto.TrainingCreateRequest;
import com.epam.training.model.Training;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("TrainingCreateRequestMapper")
class TrainingCreateRequestMapperTest {

    private final TrainingCreateRequestMapper mapper = new TrainingCreateRequestMapper();

    @Test
    @DisplayName("toEntity: maps name, date, and duration from TrainingCreateRequest to Training")
    void toEntity_mapsCurrentTrainingCreateRequest() {
        LocalDate date = LocalDate.of(2026, 1, 1);
        TrainingCreateRequest request = new TrainingCreateRequest(
                "trainee.user", "trainer.user", "Power Session", "Fitness", date, 90);

        Training training = mapper.toEntity(request);

        assertEquals("Power Session", training.getName());
        assertEquals(date, training.getDate());
        assertEquals(90, training.getDuration());
    }
}

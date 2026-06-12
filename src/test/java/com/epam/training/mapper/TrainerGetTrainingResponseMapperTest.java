package com.epam.training.mapper;

import com.epam.training.dto.GetTrainingsByTrainerResponse;
import com.epam.training.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TrainerGetTrainingResponseMapper")
class TrainerGetTrainingResponseMapperTest {

    private final TrainerGetTrainingResponseMapper mapper = new TrainerGetTrainingResponseMapper();

    @Test
    @DisplayName("toDTO: maps training name, date, duration, type and trainee username")
    void toDTO_mapsTrainingFields() {
        Training training = Training.builder()
                .name("Evening Cardio")
                .date(LocalDate.of(2024, 7, 1))
                .duration(45)
                .type(TrainingType.builder().name("Cardio").build())
                .trainee(Trainee.builder().user(User.builder().username("Trainee.One").build()).build())
                .build();

        GetTrainingsByTrainerResponse dto = mapper.toDTO(training);

        assertEquals("Evening Cardio", dto.getName());
        assertEquals(LocalDate.of(2024, 7, 1), dto.getDate());
        assertEquals(45, dto.getDuration());
        assertEquals("Cardio", dto.getType());
        assertEquals("Trainee.One", dto.getTrainee());
    }
}

package com.epam.training.mapper;

import com.epam.training.dto.GetTrainingsByTraineeResponse;
import com.epam.training.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TraineeGetTrainingsResponseMapper")
class TraineeGetTrainingsResponseMapperTest {

    private final TraineeGetTrainingsResponseMapper mapper = new TraineeGetTrainingsResponseMapper();

    @Test
    @DisplayName("toDTO: maps training name, date, duration, type and trainer username")
    void toDTO_mapsTrainingFields() {
        Training training = Training.builder()
                .name("Morning Yoga")
                .date(LocalDate.of(2024, 6, 1))
                .duration(60)
                .type(TrainingType.builder().name("Yoga").build())
                .trainer(Trainer.builder().user(User.builder().username("Trainer.One").build()).build())
                .build();

        GetTrainingsByTraineeResponse dto = mapper.toDTO(training);

        assertEquals("Morning Yoga", dto.getName());
        assertEquals(LocalDate.of(2024, 6, 1), dto.getDate());
        assertEquals(60, dto.getDuration());
        assertEquals("Yoga", dto.getType());
        assertEquals("Trainer.One", dto.getTrainer());
    }
}

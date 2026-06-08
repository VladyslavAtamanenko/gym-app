package com.epam.training.mapper;

import com.epam.training.dto.TrainingDTO;
import com.epam.training.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TrainingMapperTest {
    private final TraineeGetTrainingsResponseMapper mapper = new TraineeGetTrainingsResponseMapper();

    private static final TrainingType FITNESS = new TrainingType(1L, "Fitness");

    private Training buildTraining() {
        User user = new User(1L, "A", "B", "A.B", "pw", true);

        Trainee trainee = new Trainee();
        trainee.setId(10L);
        trainee.setUser(user);

        Trainer trainer = new Trainer();
        trainer.setId(20L);
        trainer.setUser(user);
        trainer.setSpecialization(FITNESS);

        Training t = new Training();
        t.setId(99L);
        t.setTrainee(trainee);
        t.setTrainer(trainer);
        t.setName("Core Blast");
        t.setType(FITNESS);
        t.setDate(LocalDateTime.of(2025, 10, 5, 7, 30));
        t.setDuration(Duration.ofHours(1));
        return t;
    }

    @Test
    @DisplayName("toDTO: maps all fields, trainee/trainer represented as IDs")
    void toDTO_mapsAllFields() {
        Training entity = buildTraining();

        TrainingDTO dto = mapper.toDTO(entity);

        assertEquals(99L,                               dto.getId());
        assertEquals("Core Blast",                      dto.getName());
        assertEquals(FITNESS,                           dto.getType());
        assertEquals(LocalDateTime.of(2025,10,5,7,30), dto.getDate());
        assertEquals(Duration.ofHours(1),               dto.getDuration());
        assertEquals(10L,                               dto.getTraineeId());
        assertEquals(20L,                               dto.getTrainerId());
    }

    @Test
    @DisplayName("toEntity: maps all fields, builds stub trainee/trainer from IDs")
    void toEntity_mapsAllFields() {
        TrainingDTO dto = new TrainingDTO(99L, 10L, 20L, "Core Blast", FITNESS,
                LocalDateTime.of(2025, 10, 5, 7, 30), Duration.ofHours(1));

        Training entity = mapper.toEntity(dto);

        assertEquals(99L,          entity.getId());
        assertEquals("Core Blast", entity.getName());
        assertEquals(FITNESS,      entity.getType());
        assertEquals(10L,          entity.getTrainee().getId());
        assertEquals(20L,          entity.getTrainer().getId());
    }

    @Test
    @DisplayName("round-trip toDTO → toEntity preserves all fields")
    void roundTrip() {
        Training origin  = buildTraining();
        Training rebuilt = mapper.toEntity(mapper.toDTO(origin));

        assertEquals(origin.getId(),                 rebuilt.getId());
        assertEquals(origin.getName(),               rebuilt.getName());
        assertEquals(origin.getType(),               rebuilt.getType());
        assertEquals(origin.getDate(),               rebuilt.getDate());
        assertEquals(origin.getDuration(),           rebuilt.getDuration());
        assertEquals(origin.getTrainee().getId(),    rebuilt.getTrainee().getId());
        assertEquals(origin.getTrainer().getId(),    rebuilt.getTrainer().getId());
    }
}
package com.epam.training.mapper;

import com.epam.training.dto.TrainerCreateRequest;
import com.epam.training.dto.UserCreateRequest;
import com.epam.training.model.Trainer;
import com.epam.training.model.TrainingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TrainerCreateRequestMapperTest {
    private TrainerCreateRequestMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new TrainerCreateRequestMapper();
        mapper.setUserMapper(new UserCreateRequestMapper());
    }

    @Test
    @DisplayName("toEntity: maps specialization and delegates user mapping")
    void toEntity_mapsAllFields() {
        TrainingType fitness = new TrainingType(1L, "Fitness");
        TrainerCreateRequest req = new TrainerCreateRequest(
                fitness, new UserCreateRequest("Mike", "Ross"));

        Trainer entity = mapper.toEntity(req);

        assertEquals(fitness,  entity.getSpecialization());
        assertEquals("Mike",   entity.getUser().getFirstName());
        assertEquals("Ross",   entity.getUser().getLastName());
    }

    @Test
    @DisplayName("toEntity: ID remains null")
    void toEntity_idIsNull() {
        Trainer entity = mapper.toEntity(
                new TrainerCreateRequest(null, new UserCreateRequest("A", "B")));
        assertNull(entity.getId());
    }
}
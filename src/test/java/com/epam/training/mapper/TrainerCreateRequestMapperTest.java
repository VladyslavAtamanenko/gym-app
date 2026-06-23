package com.epam.training.mapper;

import com.epam.training.dto.TrainerCreateRequest;
import com.epam.training.model.Trainer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TrainerCreateRequestMapper")
class TrainerCreateRequestMapperTest {

    private final TrainerCreateRequestMapper mapper = new TrainerCreateRequestMapper();

    @Test
    @DisplayName("toEntity: maps all fields from TrainerCreateRequest to Trainer")
    void toEntity_mapsCurrentCreateRequest() {
        TrainerCreateRequest request = new TrainerCreateRequest("Mike", "Ross", "Yoga");

        Trainer trainer = mapper.toEntity(request);

        assertNull(trainer.getId());
        assertEquals("Mike", trainer.getUser().getFirstName());
        assertEquals("Ross", trainer.getUser().getLastName());
        assertNull(trainer.getSpecialization());
    }
}

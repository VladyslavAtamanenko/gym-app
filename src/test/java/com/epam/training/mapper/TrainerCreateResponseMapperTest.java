package com.epam.training.mapper;

import com.epam.training.dto.TrainerCreateResponse;
import com.epam.training.model.Trainer;
import com.epam.training.model.TrainingType;
import com.epam.training.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TrainerCreateResponseMapperTest {
    private TrainerCreateResponseMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new TrainerCreateResponseMapper();
        mapper.setUserMapper(new UserCreateResponseMapper());
    }

    @Test
    @DisplayName("toDTO: maps id, specialization and nested user response")
    void toDTO_mapsAllFields() {
        TrainingType yoga = new TrainingType(2L, "Yoga");
        User user = new User(3L, "Mike", "Ross", "Mike.Ross", "secret", true);
        Trainer entity = new Trainer(7L, yoga, user);

        TrainerCreateResponse dto = mapper.toDTO(entity);

        assertEquals(7L,          dto.getId());
        assertEquals(yoga,        dto.getSpecialization());
        assertEquals("Mike.Ross", dto.getUser().getUsername());
        assertEquals("secret",    dto.getUser().getPassword());
    }
}
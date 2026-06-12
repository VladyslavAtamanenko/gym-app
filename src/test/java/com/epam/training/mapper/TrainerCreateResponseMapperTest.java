package com.epam.training.mapper;

import com.epam.training.dto.TrainerCreateResponse;
import com.epam.training.model.Trainer;
import com.epam.training.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("TrainerCreateResponseMapper")
class TrainerCreateResponseMapperTest {

    private final TrainerCreateResponseMapper mapper = new TrainerCreateResponseMapper();

    @Test
    @DisplayName("toDTO: maps username and password from trainer's user")
    void toDTO_mapsCredentials() {
        Trainer trainer = Trainer.builder()
                .user(new User(1L, "Mike", "Ross", "Mike.Ross", "secret", true))
                .build();

        TrainerCreateResponse dto = mapper.toDTO(trainer);

        assertEquals("Mike.Ross", dto.getUsername());
        assertEquals("secret", dto.getPassword());
    }
}

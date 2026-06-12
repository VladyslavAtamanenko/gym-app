package com.epam.training.mapper;

import com.epam.training.dto.TraineeCreateResponse;
import com.epam.training.model.Trainee;
import com.epam.training.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("TraineeCreateResponseMapper")
class TraineeCreateResponseMapperTest {

    private final TraineeCreateResponseMapper mapper = new TraineeCreateResponseMapper();

    @Test
    @DisplayName("toDTO: maps username and password from trainee's user")
    void toDTO_mapsCredentials() {
        Trainee trainee = Trainee.builder()
                .user(new User(1L, "Tom", "Ford", "Tom.Ford", "pw", true))
                .build();

        TraineeCreateResponse dto = mapper.toDTO(trainee);

        assertEquals("Tom.Ford", dto.getUsername());
        assertEquals("pw", dto.getPassword());
    }
}

package com.epam.training.mapper;

import com.epam.training.dto.TraineeCreateResponse;
import com.epam.training.model.Trainee;
import com.epam.training.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TraineeCreateResponseMapperTest {
    private TraineeCreateResponseMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new TraineeCreateResponseMapper();
        mapper.setUserMapper(new UserCreateResponseMapper());
    }

    @Test
    @DisplayName("toDTO: maps id, dateOfBirth, address and nested user response")
    void toDTO_mapsAllFields() {
        User user = new User(1L, "Tom", "Ford", "Tom.Ford", "pw", true);
        Trainee entity = new Trainee(10L, LocalDate.of(1995, 3, 15), "42 Baker St", user);

        TraineeCreateResponse dto = mapper.toDTO(entity);

        assertEquals(10L,                        dto.getId());
        assertEquals(LocalDate.of(1995, 3, 15), dto.getDateOfBirth());
        assertEquals("42 Baker St",              dto.getAddress());
        assertEquals("Tom.Ford",                 dto.getUser().getUserName());
        assertEquals("pw",                       dto.getUser().getPassword());
    }
}
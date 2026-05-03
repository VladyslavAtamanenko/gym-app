package com.epam.training.mapper;

import com.epam.training.dto.TraineeCreateRequest;
import com.epam.training.dto.UserCreateRequest;
import com.epam.training.model.Trainee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TraineeCreateRequestMapperTest {

    private TraineeCreateRequestMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new TraineeCreateRequestMapper();
        mapper.setUserMapper(new UserCreateRequestMapper());
    }

    @Test
    @DisplayName("toEntity: maps dateOfBirth, address and delegates user mapping")
    void toEntity_mapsAllFields() {
        UserCreateRequest userReq = new UserCreateRequest("Tom", "Ford");
        TraineeCreateRequest req  = new TraineeCreateRequest(
                LocalDate.of(1995, 3, 15), "42 Baker St", userReq);

        Trainee entity = mapper.toEntity(req);

        assertEquals(LocalDate.of(1995, 3, 15), entity.getDateOfBirth());
        assertEquals("42 Baker St",              entity.getAddress());
        assertNotNull(entity.getUser());
        assertEquals("Tom",  entity.getUser().getFirstName());
        assertEquals("Ford", entity.getUser().getLastName());
    }

    @Test
    @DisplayName("toEntity: ID remains null (not set from request)")
    void toEntity_idIsNull() {
        Trainee entity = mapper.toEntity(
                new TraineeCreateRequest(null, null, new UserCreateRequest("A", "B")));
        assertNull(entity.getId());
    }
}
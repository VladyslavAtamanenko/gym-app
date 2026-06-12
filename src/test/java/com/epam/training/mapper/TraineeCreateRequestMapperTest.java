package com.epam.training.mapper;

import com.epam.training.dto.TraineeCreateRequest;
import com.epam.training.model.Trainee;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TraineeCreateRequestMapper")
class TraineeCreateRequestMapperTest {

    private final TraineeCreateRequestMapper mapper = new TraineeCreateRequestMapper();

    @Test
    @DisplayName("toEntity: maps all fields from TraineeCreateRequest to Trainee")
    void toEntity_mapsCurrentCreateRequest() {
        TraineeCreateRequest request = new TraineeCreateRequest(
                "Tom", "Ford", LocalDate.of(1995, 3, 15), "42 Baker St");

        Trainee trainee = mapper.toEntity(request);

        assertNull(trainee.getId());
        assertEquals(LocalDate.of(1995, 3, 15), trainee.getDateOfBirth());
        assertEquals("42 Baker St", trainee.getAddress());
        assertEquals("Tom", trainee.getUser().getFirstName());
        assertEquals("Ford", trainee.getUser().getLastName());
    }
}

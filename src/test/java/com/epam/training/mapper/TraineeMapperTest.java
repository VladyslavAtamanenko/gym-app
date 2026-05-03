package com.epam.training.mapper;

import com.epam.training.dto.TraineeDTO;
import com.epam.training.dto.UserDTO;
import com.epam.training.model.Trainee;
import com.epam.training.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TraineeMapperTest {
    private TraineeMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new TraineeMapper();
        mapper.setUserMapper(new UserMapper());
    }

    @Test
    @DisplayName("toDTO: maps all TraineeDTO fields")
    void toDTO_mapsAllFields() {
        User user    = new User(1L, "Sue", "Gray", "Sue.Gray", "pw", true);
        Trainee entity = new Trainee(5L, LocalDate.of(1988, 7, 4), "10 Elm St", user);

        TraineeDTO dto = mapper.toDTO(entity);

        assertEquals(5L,                       dto.getId());
        assertEquals(LocalDate.of(1988, 7, 4), dto.getDateOfBirth());
        assertEquals("10 Elm St",              dto.getAddress());
        assertEquals("Sue.Gray",               dto.getUser().getUserName());
    }

    @Test
    @DisplayName("toEntity: maps all Trainee entity fields")
    void toEntity_mapsAllFields() {
        UserDTO userDTO    = new UserDTO(1L, "Sue", "Gray", "Sue.Gray", true);
        TraineeDTO dto      = new TraineeDTO(5L, LocalDate.of(1988, 7, 4), "10 Elm St", userDTO);

        Trainee entity = mapper.toEntity(dto);

        assertEquals(5L,                       entity.getId());
        assertEquals(LocalDate.of(1988, 7, 4), entity.getDateOfBirth());
        assertEquals("10 Elm St",              entity.getAddress());
        assertEquals("Sue.Gray",               entity.getUser().getUserName());
    }

    @Test
    @DisplayName("round-trip toDTO → toEntity preserves all fields")
    void roundTrip() {
        User     user   = new User(1L, "Sue", "Gray", "Sue.Gray", "pw", true);
        Trainee  origin = new Trainee(5L, LocalDate.of(1988, 7, 4), "10 Elm St", user);

        Trainee rebuilt = mapper.toEntity(mapper.toDTO(origin));

        assertEquals(origin.getId(),          rebuilt.getId());
        assertEquals(origin.getAddress(),     rebuilt.getAddress());
        assertEquals(origin.getDateOfBirth(), rebuilt.getDateOfBirth());
        assertEquals(origin.getUser().getId(),       rebuilt.getUser().getId());
        assertEquals(origin.getUser().getUserName(), rebuilt.getUser().getUserName());
    }
}
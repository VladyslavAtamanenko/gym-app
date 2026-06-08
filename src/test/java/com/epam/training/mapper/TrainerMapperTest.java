package com.epam.training.mapper;

import com.epam.training.dto.TrainerDTO;
import com.epam.training.dto.UserDTO;
import com.epam.training.model.Trainer;
import com.epam.training.model.TrainingType;
import com.epam.training.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TrainerMapperTest {
    private TrainerMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new TrainerMapper();
        mapper.setUserMapper(new UserMapper());
    }

    private static final TrainingType FITNESS = new TrainingType(1L, "Fitness");

    @Test
    @DisplayName("toDTO: maps id, specialization and user")
    void toDTO_mapsAllFields() {
        User user = new User(1L, "Lee", "Chan", "Lee.Chan", "pw", true);
        Trainer entity = new Trainer(3L, FITNESS, user);

        TrainerDTO dto = mapper.toDTO(entity);

        assertEquals(3L,       dto.getId());
        assertEquals(FITNESS,  dto.getSpecialization());
        assertEquals("Lee.Chan", dto.getUser().getUsername());
    }

    @Test
    @DisplayName("toEntity: maps id, specialization and user")
    void toEntity_mapsAllFields() {
        UserDTO userDTO = new UserDTO(1L, "Lee", "Chan", "Lee.Chan", true);
        TrainerDTO dto    = new TrainerDTO(3L, FITNESS, userDTO);

        Trainer entity = mapper.toEntity(dto);

        assertEquals(3L,      entity.getId());
        assertEquals(FITNESS, entity.getSpecialization());
        assertEquals("Lee.Chan", entity.getUser().getUsername());
    }

    @Test
    @DisplayName("round-trip toDTO → toEntity preserves all fields")
    void roundTrip() {
        User    user   = new User(1L, "Lee", "Chan", "Lee.Chan", "pw", true);
        Trainer origin = new Trainer(3L, FITNESS, user);

        Trainer rebuilt = mapper.toEntity(mapper.toDTO(origin));

        assertEquals(origin.getId(),                   rebuilt.getId());
        assertEquals(origin.getSpecialization(),       rebuilt.getSpecialization());
        assertEquals(origin.getUser().getUsername(),   rebuilt.getUser().getUsername());
    }
}
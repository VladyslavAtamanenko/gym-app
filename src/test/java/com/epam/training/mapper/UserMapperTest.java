package com.epam.training.mapper;

import com.epam.training.dto.UserDTO;
import com.epam.training.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {
    private final UserMapper mapper = new UserMapper();

    @Test
    @DisplayName("toDTO: maps all UserDTO fields; password is NOT exposed")
    void toDTO_mapsFields() {
        User entity = new User(1L, "Alice", "Brown", "Alice.Brown", "secret", true);

        UserDTO dto = mapper.toDTO(entity);

        assertEquals(1L,            dto.getId());
        assertEquals("Alice",       dto.getFirstName());
        assertEquals("Brown",       dto.getLastName());
        assertEquals("Alice.Brown", dto.getUserName());
        assertTrue(dto.getIsActive());
    }

    @Test
    @DisplayName("toEntity: maps all User fields; password remains null (not in UserDTO)")
    void toEntity_mapsFields() {
        UserDTO dto = new UserDTO(2L, "Bob", "Green", "Bob.Green", false);

        User entity = mapper.toEntity(dto);

        assertEquals(2L,          entity.getId());
        assertEquals("Bob",       entity.getFirstName());
        assertEquals("Green",     entity.getLastName());
        assertEquals("Bob.Green", entity.getUserName());
        assertFalse(entity.getIsActive());
        assertNull(entity.getPassword()); // UserDTO carries no password
    }

    @Test
    @DisplayName("toDTO → toEntity round-trip preserves all fields present in UserDTO")
    void roundTrip_dtoToEntity() {
        User original = new User(5L, "Carol", "White", "Carol.White", "pw", true);

        UserDTO dto    = mapper.toDTO(original);
        User   rebuilt = mapper.toEntity(dto);

        assertEquals(original.getId(),        rebuilt.getId());
        assertEquals(original.getFirstName(), rebuilt.getFirstName());
        assertEquals(original.getLastName(),  rebuilt.getLastName());
        assertEquals(original.getUserName(),  rebuilt.getUserName());
        assertEquals(original.getIsActive(),  rebuilt.getIsActive());
    }
}
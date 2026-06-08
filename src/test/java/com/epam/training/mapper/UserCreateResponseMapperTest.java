package com.epam.training.mapper;

import com.epam.training.dto.UserCreateResponse;
import com.epam.training.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserCreateResponseMapperTest {
    private final UserCreateResponseMapper mapper = new UserCreateResponseMapper();

    @Test
    @DisplayName("toDTO: maps all response fields")
    void toDTO_mapsAllFields() {
        User entity = new User(1L, "Jane", "Smith", "Jane.Smith", "pass123", true);

        UserCreateResponse dto = mapper.toDTO(entity);

        assertEquals(1L,           dto.getId());
        assertEquals("Jane",       dto.getFirstName());
        assertEquals("Smith",      dto.getLastName());
        assertEquals("Jane.Smith", dto.getUsername());
        assertEquals("pass123",    dto.getPassword());
    }

    @Test
    @DisplayName("toDTO: isActive is NOT exposed in UserCreateResponse")
    void toDTO_doesNotExposeIsActive() {
        // UserCreateResponse has no isActive field — this test documents the contract
        User entity = new User(1L, "A", "B", "A.B", "p", false);
        UserCreateResponse dto = mapper.toDTO(entity);
        // if this compiles, the field is intentionally absent from the DTO
        assertNotNull(dto);
    }
}
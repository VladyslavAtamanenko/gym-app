package com.epam.training.mapper;

import com.epam.training.dto.UserCreateRequest;
import com.epam.training.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserCreateRequestMapperTest {
    private final UserCreateRequestMapper mapper = new UserCreateRequestMapper();

    @Test
    @DisplayName("toEntity: maps firstName and lastName")
    void toEntity_mapsFields() {
        UserCreateRequest dto = new UserCreateRequest("John", "Doe");

        User entity = mapper.toEntity(dto);

        assertEquals("John", entity.getFirstName());
        assertEquals("Doe",  entity.getLastName());
    }

    @Test
    @DisplayName("toEntity: other User fields remain null/default")
    void toEntity_doesNotSetOtherFields() {
        User entity = mapper.toEntity(new UserCreateRequest("A", "B"));

        assertNull(entity.getId());
        assertNull(entity.getUsername());
        assertNull(entity.getPassword());
        assertNull(entity.getIsActive());
    }
}
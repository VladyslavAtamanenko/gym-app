package com.epam.training.mapper;

import com.epam.training.dto.UserCreateResponse;
import com.epam.training.dto.UserDTO;
import com.epam.training.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper implements Mapper<User, UserDTO> {
    @Override
    public UserDTO toDTO(User entity) {
        UserDTO dto = new UserDTO();
        dto.setId(entity.getId());
        dto.setFirstName(entity.getFirstName());
        dto.setLastName(entity.getLastName());
        dto.setUserName(entity.getUserName());
        dto.setIsActive(entity.getIsActive());
        return dto;
    }

    @Override
    public User toEntity(UserDTO dto) {
        User entity = new User();
        entity.setId(dto.getId());
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setUserName(dto.getUserName());
        entity.setIsActive(dto.getIsActive());
        return entity;
    }
}

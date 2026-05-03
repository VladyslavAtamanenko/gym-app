package com.epam.training.mapper;

import com.epam.training.dto.UserCreateResponse;
import com.epam.training.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserCreateResponseMapper implements ToDTOMapper<User, UserCreateResponse> {

    @Override
    public UserCreateResponse toDTO(User entity) {
        UserCreateResponse dto = new UserCreateResponse();
        dto.setId(entity.getId());
        dto.setFirstName(entity.getFirstName());
        dto.setLastName(entity.getLastName());
        dto.setUserName(entity.getUserName());
        dto.setPassword(entity.getPassword());
        return dto;
    }
}

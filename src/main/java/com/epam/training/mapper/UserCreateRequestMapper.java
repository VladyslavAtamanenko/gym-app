package com.epam.training.mapper;

import com.epam.training.dto.UserCreateRequest;
import com.epam.training.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserCreateRequestMapper implements ToEntityMapper<UserCreateRequest, User>{
    @Override
    public User toEntity(UserCreateRequest dto) {
        User entity = new User();
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        return entity;
    }
}

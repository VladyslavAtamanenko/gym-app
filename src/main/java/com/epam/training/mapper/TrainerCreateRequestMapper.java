package com.epam.training.mapper;

import com.epam.training.dto.TrainerCreateRequest;
import com.epam.training.dto.UserCreateRequest;
import com.epam.training.model.Trainer;
import com.epam.training.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TrainerCreateRequestMapper implements ToEntityMapper<TrainerCreateRequest, Trainer>{

    ToEntityMapper<UserCreateRequest, User> userMapper;

    @Override
    public Trainer toEntity(TrainerCreateRequest dto) {
        Trainer entity = new Trainer();
        entity.setSpecialization(dto.getSpecialization());
        User user = userMapper.toEntity(dto.getUser());
        entity.setUser(user);
        return entity;
    }

    @Autowired
    public void setUserMapper(ToEntityMapper<UserCreateRequest, User> userMapper) {
        this.userMapper = userMapper;
    }
}

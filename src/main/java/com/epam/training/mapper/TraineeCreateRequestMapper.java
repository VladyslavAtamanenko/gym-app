package com.epam.training.mapper;

import com.epam.training.dto.TraineeCreateRequest;
import com.epam.training.dto.UserCreateRequest;
import com.epam.training.model.Trainee;
import com.epam.training.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TraineeCreateRequestMapper implements ToEntityMapper<TraineeCreateRequest, Trainee>{

    ToEntityMapper<UserCreateRequest, User> userMapper;

    @Override
    public Trainee toEntity(TraineeCreateRequest dto) {
        Trainee entity = new Trainee();
        entity.setDateOfBirth(dto.getDateOfBirth());
        entity.setAddress(dto.getAddress());
        User user = userMapper.toEntity(dto.getUser());
        entity.setUser(user);
        return entity;
    }

    @Autowired
    public void setUserMapper(ToEntityMapper<UserCreateRequest, User> userMapper) {
        this.userMapper = userMapper;
    }
}

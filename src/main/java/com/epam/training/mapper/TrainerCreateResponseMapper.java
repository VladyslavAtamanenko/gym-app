package com.epam.training.mapper;

import com.epam.training.dto.TrainerCreateResponse;
import com.epam.training.dto.UserCreateResponse;
import com.epam.training.model.Trainer;
import com.epam.training.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TrainerCreateResponseMapper implements ToDTOMapper<Trainer, TrainerCreateResponse> {

    ToDTOMapper<User, UserCreateResponse> userMapper;

    @Override
    public TrainerCreateResponse toDTO(Trainer entity) {
        TrainerCreateResponse dto = new TrainerCreateResponse();
        dto.setId(entity.getId());
        dto.setSpecialization(entity.getSpecialization());
        UserCreateResponse user = userMapper.toDTO(entity.getUser());
        dto.setUser(user);
        return dto;
    }

    @Autowired
    public void setUserMapper(ToDTOMapper<User, UserCreateResponse> userMapper) {
        this.userMapper = userMapper;
    }
}

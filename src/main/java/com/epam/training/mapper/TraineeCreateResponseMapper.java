package com.epam.training.mapper;

import com.epam.training.dto.TraineeCreateResponse;
import com.epam.training.dto.UserCreateResponse;
import com.epam.training.model.Trainee;
import com.epam.training.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TraineeCreateResponseMapper implements ToDTOMapper<Trainee, TraineeCreateResponse>{

    ToDTOMapper<User, UserCreateResponse> userMapper;

    @Override
    public TraineeCreateResponse toDTO(Trainee entity) {
        TraineeCreateResponse dto = new TraineeCreateResponse();
        dto.setId(entity.getId());
        dto.setAddress(entity.getAddress());
        dto.setDateOfBirth(entity.getDateOfBirth());
        UserCreateResponse user = userMapper.toDTO(entity.getUser());
        dto.setUser(user);

        return dto;
    }

    @Autowired
    public void setUserMapper(ToDTOMapper<User, UserCreateResponse> userMapper) {
        this.userMapper = userMapper;
    }
}

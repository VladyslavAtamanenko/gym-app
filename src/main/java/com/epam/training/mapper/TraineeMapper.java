package com.epam.training.mapper;

import com.epam.training.dto.TraineeDTO;
import com.epam.training.dto.UserDTO;
import com.epam.training.model.Trainee;
import com.epam.training.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TraineeMapper implements Mapper<Trainee, TraineeDTO>{

    Mapper<User, UserDTO> userMapper;

    @Override
    public TraineeDTO toDTO(Trainee entity) {
        TraineeDTO dto = new TraineeDTO();
        dto.setId(entity.getId());
        dto.setAddress(entity.getAddress());
        dto.setDateOfBirth(entity.getDateOfBirth());
        UserDTO user = userMapper.toDTO(entity.getUser());
        dto.setUser(user);
        return dto;
    }

    @Override
    public Trainee toEntity(TraineeDTO dto) {
        Trainee entity = new Trainee();
        entity.setId(dto.getId());
        entity.setAddress(dto.getAddress());
        entity.setDateOfBirth(dto.getDateOfBirth());
        User user = userMapper.toEntity(dto.getUser());
        entity.setUser(user);
        return entity;
    }

    @Autowired
    public void setUserMapper(Mapper<User, UserDTO> userMapper) {
        this.userMapper = userMapper;
    }
}

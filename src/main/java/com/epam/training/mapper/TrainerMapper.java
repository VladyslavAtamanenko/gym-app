package com.epam.training.mapper;

import com.epam.training.dto.TrainerCreateResponse;
import com.epam.training.dto.TrainerDTO;
import com.epam.training.dto.UserCreateResponse;
import com.epam.training.dto.UserDTO;
import com.epam.training.model.Trainer;
import com.epam.training.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TrainerMapper implements Mapper<Trainer, TrainerDTO> {

    Mapper<User, UserDTO> userMapper;

    @Override
    public TrainerDTO toDTO(Trainer entity) {
        TrainerDTO dto = new TrainerDTO();
        dto.setId(entity.getId());
        dto.setSpecialization(entity.getSpecialization());
        UserDTO user = userMapper.toDTO(entity.getUser());
        dto.setUser(user);
        return dto;
    }

    @Override
    public Trainer toEntity(TrainerDTO dto) {
        Trainer entity = new Trainer();
        entity.setId(dto.getId());
        entity.setSpecialization(dto.getSpecialization());
        User user = userMapper.toEntity(dto.getUser());
        entity.setUser(user);
        return entity;
    }

    @Autowired
    public void setUserMapper(Mapper<User, UserDTO> userMapper) {
        this.userMapper = userMapper;
    }
}

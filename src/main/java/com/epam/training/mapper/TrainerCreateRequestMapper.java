package com.epam.training.mapper;

import com.epam.training.dto.TrainerCreateRequest;
import com.epam.training.model.Trainer;
import com.epam.training.model.User;
import org.springframework.stereotype.Component;

@Component
public class TrainerCreateRequestMapper implements ToEntityMapper<TrainerCreateRequest, Trainer> {

    @Override
    public Trainer toEntity(TrainerCreateRequest dto) {
        User user = User.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .build();
        return Trainer.builder()
                .user(user)
                .build();
    }
}

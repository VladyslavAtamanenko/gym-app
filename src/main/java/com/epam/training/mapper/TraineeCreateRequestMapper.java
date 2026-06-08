package com.epam.training.mapper;

import com.epam.training.dto.TraineeCreateRequest;
import com.epam.training.model.Trainee;
import com.epam.training.model.User;
import org.springframework.stereotype.Component;

@Component
public class TraineeCreateRequestMapper implements ToEntityMapper<TraineeCreateRequest, Trainee>{

    @Override
    public Trainee toEntity(TraineeCreateRequest dto) {
        User user = User.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .build();

        return Trainee.builder()
                .user(user)
                .dateOfBirth(dto.getDateOfBirth())
                .address(dto.getAddress())
                .build();
    }
}

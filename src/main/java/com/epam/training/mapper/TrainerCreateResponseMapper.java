package com.epam.training.mapper;

import com.epam.training.dto.TrainerCreateResponse;
import com.epam.training.model.Trainer;
import com.epam.training.model.User;
import org.springframework.stereotype.Component;

@Component
public class TrainerCreateResponseMapper implements ToDTOMapper<Trainer, TrainerCreateResponse>{

    @Override
    public TrainerCreateResponse toDTO(Trainer entity) {
        User user = entity.getUser();
        return new TrainerCreateResponse(user.getUsername(), user.getPassword());
    }
}

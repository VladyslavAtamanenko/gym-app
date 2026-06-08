package com.epam.training.mapper;

import com.epam.training.dto.TraineeCreateResponse;
import com.epam.training.model.Trainee;
import com.epam.training.model.User;
import org.springframework.stereotype.Component;

@Component
public class TraineeCreateResponseMapper implements ToDTOMapper<Trainee, TraineeCreateResponse>{

    @Override
    public TraineeCreateResponse toDTO(Trainee entity) {
        User user = entity.getUser();
        return new TraineeCreateResponse(user.getUsername(), user.getPassword());
    }
}

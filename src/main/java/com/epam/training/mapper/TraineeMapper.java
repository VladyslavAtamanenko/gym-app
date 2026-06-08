package com.epam.training.mapper;

import com.epam.training.dto.TraineeDTO;
import com.epam.training.model.Trainee;
import com.epam.training.model.User;
import org.springframework.stereotype.Component;

@Component
public class TraineeMapper implements ToDTOMapper<Trainee, TraineeDTO>{

    @Override
    public TraineeDTO toDTO(Trainee entity) {
        TraineeDTO dto = new TraineeDTO();
        User user = entity.getUser();
        dto.setUsername(user.getUsername());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        return dto;
    }
}

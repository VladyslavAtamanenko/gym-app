package com.epam.training.mapper;

import com.epam.training.dto.TrainerDTO;
import com.epam.training.model.Trainer;
import com.epam.training.model.User;
import org.springframework.stereotype.Component;

@Component
public class TrainerMapper implements ToDTOMapper<Trainer, TrainerDTO>{

    @Override
    public TrainerDTO toDTO(Trainer entity) {
        TrainerDTO dto = new TrainerDTO();
        User user = entity.getUser();
        dto.setUsername(user.getUsername());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setSpecialization(entity.getSpecialization().getName());
        return dto;
    }
}


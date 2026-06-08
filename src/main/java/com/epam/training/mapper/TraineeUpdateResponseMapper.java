package com.epam.training.mapper;

import com.epam.training.dto.TraineeGetResponse;
import com.epam.training.dto.TraineeUpdateResponse;
import com.epam.training.dto.TrainerDTO;
import com.epam.training.model.Trainee;
import com.epam.training.model.Trainer;
import com.epam.training.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TraineeUpdateResponseMapper implements ToDTOMapper<Trainee, TraineeUpdateResponse>{

    private ToDTOMapper<Trainer, TrainerDTO> trainerMapper;

    @Override
    public TraineeUpdateResponse toDTO(Trainee entity) {
        TraineeUpdateResponse dto = new TraineeUpdateResponse();
        User user = entity.getUser();
        dto.setUsername(user.getUsername());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setAddress(entity.getAddress());
        dto.setDateOfBirth(entity.getDateOfBirth());
        dto.setIsActive(user.getIsActive());
        List<TrainerDTO> trainers = entity.getTrainers().stream()
                .map(trainerMapper::toDTO)
                .toList();
        dto.setTrainers(trainers);
        return dto;
    }

    @Autowired
    public void setTrainerMapper(ToDTOMapper<Trainer, TrainerDTO> trainerMapper) {
        this.trainerMapper = trainerMapper;
    }
}

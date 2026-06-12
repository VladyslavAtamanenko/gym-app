package com.epam.training.mapper;

import com.epam.training.dto.TraineeGetResponse;
import com.epam.training.dto.TrainerDTO;
import com.epam.training.model.Trainer;
import com.epam.training.model.Trainee;
import com.epam.training.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class TraineeGetResponseMapper implements ToDTOMapper<Trainee, TraineeGetResponse>{

    private ToDTOMapper<Trainer, TrainerDTO> trainerMapper;

    @Override
    public TraineeGetResponse toDTO(Trainee entity) {
        TraineeGetResponse dto = new TraineeGetResponse();
        User user = entity.getUser();
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setAddress(entity.getAddress());
        dto.setDateOfBirth(entity.getDateOfBirth());
        dto.setIsActive(user.getIsActive());
        List<TrainerDTO> trainers = Optional.ofNullable(entity.getTrainers()).orElse(List.of()).stream()
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

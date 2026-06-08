package com.epam.training.mapper;

import com.epam.training.dto.GetTrainingsByTrainerResponse;
import com.epam.training.model.Training;
import org.springframework.stereotype.Component;

@Component
public class TrainerGetTrainingResponseMapper implements ToDTOMapper<Training, GetTrainingsByTrainerResponse>{
    @Override
    public GetTrainingsByTrainerResponse toDTO(Training entity) {
        GetTrainingsByTrainerResponse dto = new GetTrainingsByTrainerResponse();
        dto.setDate(entity.getDate());
        dto.setDuration(entity.getDuration());
        dto.setName(entity.getName());
        dto.setType(entity.getType().getName());
        dto.setTrainee(entity.getTrainee().getUser().getUsername());
        return dto;
    }
}

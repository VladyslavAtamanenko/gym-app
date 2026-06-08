package com.epam.training.mapper;

import com.epam.training.dto.GetTrainingsByTraineeResponse;
import com.epam.training.model.Training;
import org.springframework.stereotype.Component;

@Component
public class TraineeGetTrainingsResponseMapper implements ToDTOMapper<Training, GetTrainingsByTraineeResponse>{
    @Override
    public GetTrainingsByTraineeResponse toDTO(Training entity) {
        GetTrainingsByTraineeResponse dto = new GetTrainingsByTraineeResponse();
        dto.setDate(entity.getDate());
        dto.setDuration(entity.getDuration());
        dto.setName(entity.getName());
        dto.setType(entity.getType().getName());
        dto.setTrainer(entity.getTrainer().getUser().getUsername());
        return dto;
    }
}

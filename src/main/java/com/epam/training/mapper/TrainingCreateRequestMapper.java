package com.epam.training.mapper;

import com.epam.training.dto.TrainingCreateRequest;
import com.epam.training.model.Trainee;
import com.epam.training.model.Trainer;
import com.epam.training.model.Training;
import org.springframework.stereotype.Component;


@Component
public class TrainingCreateRequestMapper implements ToEntityMapper<TrainingCreateRequest, Training> {
    @Override
    public Training toEntity(TrainingCreateRequest dto) {
        Training entity = new Training();

        Trainee trainee = Trainee.builder()
                .id(dto.getTraineeId())
                .build();

        Trainer trainer = Trainer.builder()
                .id(dto.getTrainerId())
                .build();

        entity.setTrainee(trainee);
        entity.setTrainer(trainer);
        entity.setName(dto.getName());
        entity.setDate(dto.getDate());
        entity.setType(dto.getType());
        entity.setDuration(dto.getDuration());

        return entity;
    }
}

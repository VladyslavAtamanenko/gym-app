package com.epam.training.mapper;

import com.epam.training.dto.TrainingCreateRequest;
import com.epam.training.model.*;
import org.springframework.stereotype.Component;


@Component
public class TrainingCreateRequestMapper implements ToEntityMapper<TrainingCreateRequest, Training> {
    @Override
    public Training toEntity(TrainingCreateRequest dto) {
        Training entity = new Training();

        User traineeInfo = User.builder()
                .username(dto.getTrainee())
                .build();
        Trainee trainee = Trainee.builder()
                .user(traineeInfo)
                .build();

        User trainerInfo = User.builder()
                .username(dto.getTrainer())
                .build();
        Trainer trainer = Trainer.builder()
                .user(trainerInfo)
                .build();

        TrainingType type = TrainingType.builder()
                .name(dto.getType())
                .build();

        entity.setTrainee(trainee);
        entity.setTrainer(trainer);
        entity.setName(dto.getName());
        entity.setDate(dto.getDate());
        entity.setType(type);
        entity.setDuration(dto.getDuration());

        return entity;
    }
}

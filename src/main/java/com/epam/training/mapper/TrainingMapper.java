package com.epam.training.mapper;

import com.epam.training.dto.TrainingDTO;
import com.epam.training.model.Trainee;
import com.epam.training.model.Trainer;
import com.epam.training.model.Training;
import org.springframework.stereotype.Component;

@Component
public class TrainingMapper implements Mapper<Training, TrainingDTO>{
    @Override
    public TrainingDTO toDTO(Training entity) {
        TrainingDTO dto = new TrainingDTO();
        dto.setId(entity.getId());
        dto.setDate(entity.getDate());
        dto.setDuration(entity.getDuration());
        dto.setName(entity.getName());
        dto.setType(entity.getType());
        dto.setTraineeId(entity.getTrainee().getId());
        dto.setTrainerId(entity.getTrainer().getId());
        return dto;
    }

    @Override
    public Training toEntity(TrainingDTO dto) {
        Training entity = new Training();

        Trainee trainee = Trainee.builder()
                .id(dto.getTraineeId())
                .build();

        Trainer trainer = Trainer.builder()
                .id(dto.getTrainerId())
                .build();

        entity.setId(dto.getId());
        entity.setTrainee(trainee);
        entity.setTrainer(trainer);
        entity.setName(dto.getName());
        entity.setDate(dto.getDate());
        entity.setType(dto.getType());
        entity.setDuration(dto.getDuration());

        return entity;
    }
}

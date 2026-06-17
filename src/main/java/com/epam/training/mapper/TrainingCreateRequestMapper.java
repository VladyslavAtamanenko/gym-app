package com.epam.training.mapper;

import com.epam.training.dto.TrainingCreateRequest;
import com.epam.training.model.Training;
import org.springframework.stereotype.Component;


@Component
public class TrainingCreateRequestMapper implements ToEntityMapper<TrainingCreateRequest, Training> {
    @Override
    public Training toEntity(TrainingCreateRequest dto) {
        Training entity = new Training();
        entity.setName(dto.getName());
        entity.setDate(dto.getDate());
        entity.setDuration(dto.getDuration());
        return entity;
    }
}

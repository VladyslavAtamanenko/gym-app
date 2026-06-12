package com.epam.training.mapper;

import com.epam.training.dto.TraineeDTO;
import com.epam.training.dto.TrainerGetResponse;
import com.epam.training.dto.TrainerUpdateResponse;
import com.epam.training.model.Trainee;
import com.epam.training.model.Trainer;
import com.epam.training.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class TrainerUpdateResponseMapper implements ToDTOMapper<Trainer, TrainerUpdateResponse>{

    private ToDTOMapper<Trainee, TraineeDTO> traineeMapper;

    @Override
    public TrainerUpdateResponse toDTO(Trainer entity) {
        TrainerUpdateResponse dto = new TrainerUpdateResponse();
        User user = entity.getUser();
        dto.setUsername(user.getUsername());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setIsActive(user.getIsActive());
        dto.setSpecialization(entity.getSpecialization().getName());
        List<TraineeDTO> trainees = Optional.ofNullable(entity.getTrainees()).orElse(List.of()).stream()
                .map(traineeMapper::toDTO)
                .toList();
        dto.setTrainees(trainees);
        return dto;
    }

    @Autowired
    public void setTraineeMapper(ToDTOMapper<Trainee, TraineeDTO> traineeMapper) {
        this.traineeMapper = traineeMapper;
    }
}

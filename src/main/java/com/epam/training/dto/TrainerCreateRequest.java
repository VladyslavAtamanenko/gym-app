package com.epam.training.dto;

import com.epam.training.model.TrainingType;
import com.epam.training.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainerCreateRequest {
    private TrainingType specialization;
    private UserCreateRequest user;
}

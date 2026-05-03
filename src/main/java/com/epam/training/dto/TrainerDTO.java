package com.epam.training.dto;

import com.epam.training.model.TrainingType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainerDTO {
    private Long id;
    private TrainingType specialization;
    private UserDTO user;
}

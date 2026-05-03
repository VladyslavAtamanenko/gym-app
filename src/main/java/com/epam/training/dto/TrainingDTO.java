package com.epam.training.dto;

import com.epam.training.model.Trainee;
import com.epam.training.model.Trainer;
import com.epam.training.model.TrainingType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainingDTO {
    private Long id;
    private Long traineeId;
    private Long trainerId;
    private String name;
    private TrainingType type;
    private LocalDateTime date;
    private Duration duration;
}

package com.epam.training.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Training {
    private Long id;
    private Trainee trainee;
    private Trainer trainer;
    private String name;
    private TrainingType type;
    private LocalDateTime date;
    private Duration duration;
}

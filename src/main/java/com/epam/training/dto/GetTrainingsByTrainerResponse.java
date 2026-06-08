package com.epam.training.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetTrainingsByTrainerResponse {
    private String name;
    private String type;
    private LocalDateTime date;
    private Duration duration;
    private String trainee;
}

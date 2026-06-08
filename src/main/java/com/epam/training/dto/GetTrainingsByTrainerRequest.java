package com.epam.training.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetTrainingsByTrainerRequest {
    private String username;
    private LocalDateTime from;
    private LocalDateTime to;
    private String trainee;
}

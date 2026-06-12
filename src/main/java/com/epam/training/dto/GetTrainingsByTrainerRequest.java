package com.epam.training.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetTrainingsByTrainerRequest {
    private String username;
    private LocalDate from;
    private LocalDate to;
    private String trainee;
}

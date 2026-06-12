package com.epam.training.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetTrainingsByTraineeRequest {
    private String username;
    private LocalDate from;
    private LocalDate to;
    private String trainer;
    private String trainingType;
}

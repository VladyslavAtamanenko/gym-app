package com.epam.training.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetTrainingsByTraineeRequest {
    private String username;
    private LocalDateTime from;
    private LocalDateTime to;
    private String trainer;
    private String trainingType;
}

package com.epam.training.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainingCreateRequest {
    private String trainee;
    private String trainer;
    private String name;
    private String type;
    private LocalDate date;
    private Integer duration;
}

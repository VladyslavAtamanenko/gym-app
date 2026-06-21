package com.epam.training.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainingCreateRequest {
    @NotBlank
    private String trainee;
    @NotBlank
    private String trainer;
    @NotBlank
    private String name;
    private String type;
    @NotNull
    private LocalDate date;
    @NotNull
    @Positive
    private Integer duration;
}

package com.epam.training.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainerUpdateRequest {
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    private String specialization;
    @NotNull
    private Boolean isActive;
}

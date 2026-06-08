package com.epam.training.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainerUpdateRequest {
    private String username;
    private String firstName;
    private String lastName;
    private String specialization;
    private Boolean isActive;
}

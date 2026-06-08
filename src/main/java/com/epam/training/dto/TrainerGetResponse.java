package com.epam.training.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainerGetResponse {
    private String firstName;
    private String lastName;
    private String specialization;
    private Boolean isActive;
    private List<TraineeDTO> trainees;
}

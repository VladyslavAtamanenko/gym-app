package com.epam.training.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TraineeCreateRequest {
    private LocalDate dateOfBirth;
    private String address;
    private UserCreateRequest user;
}

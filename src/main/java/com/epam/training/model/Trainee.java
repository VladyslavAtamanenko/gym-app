package com.epam.training.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Trainee{
    private Long id;
    private LocalDate dateOfBirth;
    private String address;
    private User user;
}

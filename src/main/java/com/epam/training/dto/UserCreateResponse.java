package com.epam.training.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String userName;
    private String password;
}

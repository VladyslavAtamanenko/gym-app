package com.epam.training.exception;

public class TraineeNotFoundException extends RuntimeException {
    public TraineeNotFoundException(String username) {
        super("Trainee not found. traineeUsername=" + username);
    }
}

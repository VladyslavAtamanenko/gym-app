package com.epam.training.exception;

public class TrainerNotFoundException extends RuntimeException {
    public TrainerNotFoundException(String username) {
        super("Trainer not found. trainerUsername=" + username);
    }
}

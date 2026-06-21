package com.epam.training.exception;

public class TrainingTypeNotFoundException extends RuntimeException {
    public TrainingTypeNotFoundException(String name) {
        super("Training type not found. trainingTypeName=" + name);
    }

    public TrainingTypeNotFoundException(Long id) {
        super("Training type not found. trainingTypeId=" + id);
    }
}

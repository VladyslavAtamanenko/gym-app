package com.epam.training.dao;

import com.epam.training.model.*;

import java.time.LocalDate;

final class DaoTestSupport {

    private DaoTestSupport() {
    }

    static User user(String firstName, String lastName, String username) {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setPassword("password");
        user.setIsActive(true);
        return user;
    }

    static Trainee trainee(String username, LocalDate dateOfBirth, String address) {
        Trainee trainee = new Trainee();
        trainee.setUser(user("Trainee", "User", username));
        trainee.setDateOfBirth(dateOfBirth);
        trainee.setAddress(address);
        return trainee;
    }

    static Trainer trainer(String username, TrainingType specialization) {
        Trainer trainer = new Trainer();
        trainer.setUser(user("Trainer", "User", username));
        trainer.setSpecialization(specialization);
        return trainer;
    }

    static Training training(String name, LocalDate date, int duration,
                             Trainee trainee, Trainer trainer, TrainingType type) {
        Training training = new Training();
        training.setName(name);
        training.setDate(date);
        training.setDuration(duration);
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setType(type);
        return training;
    }
}

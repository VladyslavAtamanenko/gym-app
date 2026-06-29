package com.epam.training.security;

import com.epam.training.dto.TrainingCreateRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("gymSecurity")
public class GymSecurity {

    public boolean isOwner(String username, Authentication auth) {
        return auth.getName().equals(username);
    }

    public boolean isTrainingParticipant(TrainingCreateRequest req, Authentication auth) {
        String name = auth.getName();
        return name.equals(req.getTrainee()) || name.equals(req.getTrainer());
    }
}

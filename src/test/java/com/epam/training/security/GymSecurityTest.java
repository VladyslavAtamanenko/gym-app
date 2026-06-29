package com.epam.training.security;

import com.epam.training.dto.TrainingCreateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("GymSecurity")
class GymSecurityTest {

    private GymSecurity gymSecurity;

    @BeforeEach
    void setUp() {
        gymSecurity = new GymSecurity();
    }

    private Authentication auth(String username) {
        return new UsernamePasswordAuthenticationToken(username, null, List.of());
    }

    // --- isOwner ---

    @Test
    @DisplayName("isOwner: returns true when username matches authenticated user")
    void isOwner_returnsTrueForMatchingUsername() {
        assertThat(gymSecurity.isOwner("John.Doe", auth("John.Doe"))).isTrue();
    }

    @Test
    @DisplayName("isOwner: returns false when username differs from authenticated user")
    void isOwner_returnsFalseForDifferentUsername() {
        assertThat(gymSecurity.isOwner("John.Doe", auth("Other.User"))).isFalse();
    }

    // --- isTrainingParticipant ---

    @Test
    @DisplayName("isTrainingParticipant: returns true when auth user is the trainee")
    void isTrainingParticipant_trueForTrainee() {
        TrainingCreateRequest req = new TrainingCreateRequest();
        req.setTrainee("John.Doe");
        req.setTrainer("Alice.Smith");

        assertThat(gymSecurity.isTrainingParticipant(req, auth("John.Doe"))).isTrue();
    }

    @Test
    @DisplayName("isTrainingParticipant: returns true when auth user is the trainer")
    void isTrainingParticipant_trueForTrainer() {
        TrainingCreateRequest req = new TrainingCreateRequest();
        req.setTrainee("John.Doe");
        req.setTrainer("Alice.Smith");

        assertThat(gymSecurity.isTrainingParticipant(req, auth("Alice.Smith"))).isTrue();
    }

    @Test
    @DisplayName("isTrainingParticipant: returns false when auth user is neither participant")
    void isTrainingParticipant_falseForUnrelatedUser() {
        TrainingCreateRequest req = new TrainingCreateRequest();
        req.setTrainee("John.Doe");
        req.setTrainer("Alice.Smith");

        assertThat(gymSecurity.isTrainingParticipant(req, auth("Bob.Jones"))).isFalse();
    }

    @Test
    @DisplayName("isTrainingParticipant: returns false when auth user is a different trainer")
    void isTrainingParticipant_falseForAuthorityOnlyGrantedAuthority() {
        Authentication auth = new UsernamePasswordAuthenticationToken(
                "Other.Trainer", null,
                List.of(new SimpleGrantedAuthority("ROLE_TRAINER")));
        TrainingCreateRequest req = new TrainingCreateRequest();
        req.setTrainee("John.Doe");
        req.setTrainer("Alice.Smith");

        assertThat(gymSecurity.isTrainingParticipant(req, auth)).isFalse();
    }
}

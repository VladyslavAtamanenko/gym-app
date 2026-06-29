package com.epam.training.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("LoginAttemptService")
class LoginAttemptServiceTest {

    private LoginAttemptService service;

    @BeforeEach
    void setUp() {
        service = new LoginAttemptService();
    }

    @Test
    @DisplayName("isBlocked: returns false for unknown user")
    void isBlocked_falseForUnknownUser() {
        assertThat(service.isBlocked("John.Doe")).isFalse();
    }

    @Test
    @DisplayName("isBlocked: returns false after fewer than 3 failures")
    void isBlocked_falseBeforeMaxAttempts() {
        service.loginFailed("John.Doe");
        service.loginFailed("John.Doe");
        assertThat(service.isBlocked("John.Doe")).isFalse();
    }

    @Test
    @DisplayName("isBlocked: returns true after exactly 3 failures")
    void isBlocked_trueAfterMaxAttempts() {
        service.loginFailed("John.Doe");
        service.loginFailed("John.Doe");
        service.loginFailed("John.Doe");
        assertThat(service.isBlocked("John.Doe")).isTrue();
    }

    @Test
    @DisplayName("isBlocked: returns true after more than 3 failures")
    void isBlocked_trueAfterMoreThanMaxAttempts() {
        for (int i = 0; i < 5; i++) service.loginFailed("John.Doe");
        assertThat(service.isBlocked("John.Doe")).isTrue();
    }

    @Test
    @DisplayName("loginSucceeded: clears block after successful login")
    void loginSucceeded_clearsBlock() {
        service.loginFailed("John.Doe");
        service.loginFailed("John.Doe");
        service.loginFailed("John.Doe");
        assertThat(service.isBlocked("John.Doe")).isTrue();

        service.loginSucceeded("John.Doe");
        assertThat(service.isBlocked("John.Doe")).isFalse();
    }

    @Test
    @DisplayName("failure counter is per-user: blocking one user does not affect another")
    void failureCounter_isPerUser() {
        service.loginFailed("John.Doe");
        service.loginFailed("John.Doe");
        service.loginFailed("John.Doe");
        assertThat(service.isBlocked("Alice.Smith")).isFalse();
    }

    @Test
    @DisplayName("loginSucceeded: resets counter so user can fail again from 0")
    void loginSucceeded_resetsCounter() {
        service.loginFailed("John.Doe");
        service.loginFailed("John.Doe");
        service.loginSucceeded("John.Doe");

        service.loginFailed("John.Doe");
        service.loginFailed("John.Doe");
        assertThat(service.isBlocked("John.Doe")).isFalse();
    }
}

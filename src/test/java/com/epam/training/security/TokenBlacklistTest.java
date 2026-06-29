package com.epam.training.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TokenBlacklist")
class TokenBlacklistTest {

    private TokenBlacklist tokenBlacklist;

    @BeforeEach
    void setUp() {
        tokenBlacklist = new TokenBlacklist();
    }

    @Test
    @DisplayName("contains: returns false for token that was never added")
    void contains_falseForUnknownToken() {
        assertThat(tokenBlacklist.contains("some.jwt.token")).isFalse();
    }

    @Test
    @DisplayName("add + contains: returns true after token is blacklisted")
    void contains_trueAfterAdd() {
        tokenBlacklist.add("some.jwt.token");
        assertThat(tokenBlacklist.contains("some.jwt.token")).isTrue();
    }
}

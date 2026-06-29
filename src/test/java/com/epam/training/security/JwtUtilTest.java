package com.epam.training.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JwtUtil")
class JwtUtilTest {

    private static final String SECRET =
            "bG9jYWxkZXZzZWNyZXRrZXkxMjM0NTY3ODkwYWJjZGVmZ2hpamtsbW5vcHFyc3Q=";

    private JwtUtil jwtUtil;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", SECRET);
        ReflectionTestUtils.setField(jwtUtil, "expirationMs", 3_600_000L);

        userDetails = User.builder()
                .username("John.Doe")
                .password("irrelevant")
                .authorities(new SimpleGrantedAuthority("ROLE_TRAINEE"))
                .build();
    }

    @Test
    @DisplayName("generate: produces a non-blank token")
    void generate_producesNonBlankToken() {
        assertThat(jwtUtil.generate(userDetails)).isNotBlank();
    }

    @Test
    @DisplayName("extractUsername: returns correct subject from generated token")
    void extractUsername_returnsCorrectSubject() {
        String token = jwtUtil.generate(userDetails);
        assertThat(jwtUtil.extractUsername(token)).isEqualTo("John.Doe");
    }

    @Test
    @DisplayName("isValid: returns true for freshly generated token and matching user")
    void isValid_trueForFreshToken() {
        String token = jwtUtil.generate(userDetails);
        assertThat(jwtUtil.isValid(token, userDetails)).isTrue();
    }

    @Test
    @DisplayName("isValid: returns false when username does not match token subject")
    void isValid_falseForWrongUser() {
        String token = jwtUtil.generate(userDetails);
        UserDetails other = User.builder()
                .username("Other.User")
                .password("irrelevant")
                .authorities(new SimpleGrantedAuthority("ROLE_TRAINEE"))
                .build();
        assertThat(jwtUtil.isValid(token, other)).isFalse();
    }

    @Test
    @DisplayName("isValid: returns false for an expired token")
    void isValid_falseForExpiredToken() {
        ReflectionTestUtils.setField(jwtUtil, "expirationMs", -1000L);
        String token = jwtUtil.generate(userDetails);
        assertThat(jwtUtil.isValid(token, userDetails)).isFalse();
    }

    @Test
    @DisplayName("isValid: returns false for a tampered token")
    void isValid_falseForTamperedToken() {
        String token = jwtUtil.generate(userDetails) + "tampered";
        assertThat(jwtUtil.isValid(token, userDetails)).isFalse();
    }
}

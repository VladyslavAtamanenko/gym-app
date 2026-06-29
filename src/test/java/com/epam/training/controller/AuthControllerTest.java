package com.epam.training.controller;

import com.epam.training.dto.LoginRequest;
import com.epam.training.security.JwtUtil;
import com.epam.training.security.LoginAttemptService;
import com.epam.training.security.TokenBlacklist;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController")
class AuthControllerTest {

    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtUtil jwtUtil;
    @Mock private UserDetailsService userDetailsService;
    @Mock private LoginAttemptService loginAttemptService;
    @Mock private TokenBlacklist tokenBlacklist;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new AuthController(
                        authenticationManager, jwtUtil, userDetailsService,
                        loginAttemptService, tokenBlacklist))
                .build();
    }

    // --- POST /auth/login ---

    @Test
    @DisplayName("login: returns 429 when account is locked")
    void login_returns429_whenBlocked() throws Exception {
        when(loginAttemptService.isBlocked("John.Doe")).thenReturn(true);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest("John.Doe", "pw"))))
                .andExpect(status().isTooManyRequests());
    }

    @Test
    @DisplayName("login: returns 401 on bad credentials and records failure")
    void login_returns401_andRecordsFailure_onBadCredentials() throws Exception {
        when(loginAttemptService.isBlocked("John.Doe")).thenReturn(false);
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("bad"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest("John.Doe", "wrong"))))
                .andExpect(status().isUnauthorized());

        verify(loginAttemptService).loginFailed("John.Doe");
    }

    @Test
    @DisplayName("login: returns 200 with token on success and clears failure counter")
    void login_returns200WithToken_onSuccess() throws Exception {
        when(loginAttemptService.isBlocked("John.Doe")).thenReturn(false);
        when(userDetailsService.loadUserByUsername("John.Doe")).thenReturn(
                User.withUsername("John.Doe").password("pw")
                        .authorities(new SimpleGrantedAuthority("ROLE_TRAINEE")).build());
        when(jwtUtil.generate(any())).thenReturn("generated.jwt.token");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest("John.Doe", "secret"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("generated.jwt.token"));

        verify(loginAttemptService).loginSucceeded("John.Doe");
    }

    // --- POST /auth/logout ---

    @Test
    @DisplayName("logout: returns 204 and blacklists the token")
    void logout_returns204_andBlacklists() throws Exception {
        mockMvc.perform(post("/auth/logout")
                        .header("Authorization", "Bearer my.jwt.token"))
                .andExpect(status().isNoContent());

        verify(tokenBlacklist).add("my.jwt.token");
    }

    @Test
    @DisplayName("logout: ignores Authorization header without Bearer prefix")
    void logout_ignoresNonBearerHeader() throws Exception {
        mockMvc.perform(post("/auth/logout")
                        .header("Authorization", "Basic dXNlcjpwYXNz"))
                .andExpect(status().isNoContent());

        verify(tokenBlacklist, never()).add(any());
    }
}

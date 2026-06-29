package com.epam.training.controller;

import com.epam.training.security.UserDetailsServiceImpl;
import com.epam.training.service.TraineeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
@DisplayName("TraineeController — security")
class TraineeControllerSecurityTest {

    @MockBean private TraineeService traineeService;
    @MockBean private UserDetailsServiceImpl userDetailsService;

    @Autowired private MockMvc mockMvc;

    // --- POST /trainees (open endpoint) ---

    @Test
    @DisplayName("register: accessible without authentication")
    void register_isOpen() throws Exception {
        mockMvc.perform(post("/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"firstName":"John","lastName":"Doe"}
                                """))
                .andExpect(status().is2xxSuccessful());
    }

    // --- GET /trainees/{username} ---

    @Test
    @WithMockUser(roles = "TRAINEE", username = "John.Doe")
    @DisplayName("getByUsername: allows owner")
    void getByUsername_allowsOwner() throws Exception {
        mockMvc.perform(get("/trainees/John.Doe"));
        verify(traineeService).findByUsername("John.Doe");
    }

    @Test
    @WithMockUser(roles = "TRAINEE", username = "Other.User")
    @DisplayName("getByUsername: blocks trainee with different username (403)")
    void getByUsername_blocksOtherTrainee() throws Exception {
        mockMvc.perform(get("/trainees/John.Doe"))
                .andExpect(status().isForbidden());
        verify(traineeService, never()).findByUsername(any());
    }

    @Test
    @WithMockUser(roles = "TRAINER", username = "Alice.Smith")
    @DisplayName("getByUsername: blocks trainer role (403)")
    void getByUsername_blocksTrainer() throws Exception {
        mockMvc.perform(get("/trainees/John.Doe"))
                .andExpect(status().isForbidden());
        verify(traineeService, never()).findByUsername(any());
    }

    // --- DELETE /trainees/{username} ---

    @Test
    @WithMockUser(roles = "TRAINEE", username = "John.Doe")
    @DisplayName("delete: allows owner")
    void delete_allowsOwner() throws Exception {
        mockMvc.perform(delete("/trainees/John.Doe"));
        verify(traineeService).delete("John.Doe");
    }

    @Test
    @WithMockUser(roles = "TRAINEE", username = "Other.User")
    @DisplayName("delete: blocks non-owner (403)")
    void delete_blocksNonOwner() throws Exception {
        mockMvc.perform(delete("/trainees/John.Doe"))
                .andExpect(status().isForbidden());
        verify(traineeService, never()).delete(any());
    }

    // --- PUT /trainees/{username}/password ---

    @Test
    @WithMockUser(roles = "TRAINEE", username = "John.Doe")
    @DisplayName("changePassword: allows owner")
    void changePassword_allowsOwner() throws Exception {
        mockMvc.perform(put("/trainees/John.Doe/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"username":"John.Doe","oldPassword":"old","newPassword":"new"}
                        """));
        verify(traineeService).changePassword(any());
    }

    @Test
    @WithMockUser(roles = "TRAINER", username = "Alice.Smith")
    @DisplayName("changePassword: blocks trainer role (403)")
    void changePassword_blocksTrainer() throws Exception {
        mockMvc.perform(put("/trainees/John.Doe/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"John.Doe","oldPassword":"old","newPassword":"new"}
                                """))
                .andExpect(status().isForbidden());
        verify(traineeService, never()).changePassword(any());
    }

    // --- PATCH /trainees/{username} ---

    @Test
    @WithMockUser(roles = "TRAINEE", username = "John.Doe")
    @DisplayName("setActive: allows owner")
    void setActive_allowsOwner() throws Exception {
        mockMvc.perform(patch("/trainees/John.Doe")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"isActive":true}
                        """));
        verify(traineeService).activate("John.Doe");
    }

    @Test
    @WithMockUser(roles = "TRAINER")
    @DisplayName("setActive: blocks trainer role (403)")
    void setActive_blocksTrainer() throws Exception {
        mockMvc.perform(patch("/trainees/John.Doe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"isActive":true}
                                """))
                .andExpect(status().isForbidden());
        verify(traineeService, never()).activate(any());
    }
}

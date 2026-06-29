package com.epam.training.controller;

import com.epam.training.security.UserDetailsServiceImpl;
import com.epam.training.service.TrainerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
@DisplayName("TrainerController — security")
class TrainerControllerSecurityTest {

    @MockBean private TrainerService trainerService;
    @MockBean private UserDetailsServiceImpl userDetailsService;

    @Autowired private MockMvc mockMvc;

    // --- POST /trainers (open endpoint) ---

    @Test
    @DisplayName("register: accessible without authentication")
    void register_isOpen() throws Exception {
        mockMvc.perform(post("/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"firstName":"Alice","lastName":"Smith","specialization":"Yoga"}
                                """))
                .andExpect(status().is2xxSuccessful());
    }

    // --- GET /trainers/{username} ---

    @Test
    @WithMockUser(roles = "TRAINER", username = "Alice.Smith")
    @DisplayName("getByUsername: allows owner")
    void getByUsername_allowsOwner() throws Exception {
        mockMvc.perform(get("/trainers/Alice.Smith"));
        verify(trainerService).findByUsername("Alice.Smith");
    }

    @Test
    @WithMockUser(roles = "TRAINEE", username = "John.Doe")
    @DisplayName("getByUsername: blocks trainee role (403)")
    void getByUsername_blocksTrainee() throws Exception {
        mockMvc.perform(get("/trainers/Alice.Smith"))
                .andExpect(status().isForbidden());
        verify(trainerService, never()).findByUsername(any());
    }

    @Test
    @WithMockUser(roles = "TRAINER", username = "Other.Trainer")
    @DisplayName("getByUsername: blocks trainer with different username (403)")
    void getByUsername_blocksOtherTrainer() throws Exception {
        mockMvc.perform(get("/trainers/Alice.Smith"))
                .andExpect(status().isForbidden());
        verify(trainerService, never()).findByUsername(any());
    }

    // --- GET /trainers/not-assigned/{username} ---

    @Test
    @WithMockUser(roles = "TRAINEE", username = "John.Doe")
    @DisplayName("getNotAssigned: allows trainee owner")
    void getNotAssigned_allowsTraineeOwner() throws Exception {
        when(trainerService.findNotAssignedOnTrainee(any(), any())).thenReturn(Page.empty());
        mockMvc.perform(get("/trainers/not-assigned/John.Doe"));
        verify(trainerService).findNotAssignedOnTrainee(eq("John.Doe"), any());
    }

    @Test
    @WithMockUser(roles = "TRAINER", username = "Alice.Smith")
    @DisplayName("getNotAssigned: blocks trainer role (403)")
    void getNotAssigned_blocksTrainer() throws Exception {
        mockMvc.perform(get("/trainers/not-assigned/John.Doe"))
                .andExpect(status().isForbidden());
        verify(trainerService, never()).findNotAssignedOnTrainee(any(), any());
    }

    // --- PUT /trainers/{username}/password ---

    @Test
    @WithMockUser(roles = "TRAINER", username = "Alice.Smith")
    @DisplayName("changePassword: allows owner")
    void changePassword_allowsOwner() throws Exception {
        mockMvc.perform(put("/trainers/Alice.Smith/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"username":"Alice.Smith","oldPassword":"old","newPassword":"new"}
                        """));
        verify(trainerService).changePassword(any());
    }

    @Test
    @WithMockUser(roles = "TRAINEE", username = "John.Doe")
    @DisplayName("changePassword: blocks trainee role (403)")
    void changePassword_blocksTrainee() throws Exception {
        mockMvc.perform(put("/trainers/Alice.Smith/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"Alice.Smith","oldPassword":"old","newPassword":"new"}
                                """))
                .andExpect(status().isForbidden());
        verify(trainerService, never()).changePassword(any());
    }

    // --- PATCH /trainers/{username} ---

    @Test
    @WithMockUser(roles = "TRAINER", username = "Alice.Smith")
    @DisplayName("setActive: allows owner")
    void setActive_allowsOwner() throws Exception {
        mockMvc.perform(patch("/trainers/Alice.Smith")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"isActive":false}
                        """));
        verify(trainerService).deactivate("Alice.Smith");
    }

    @Test
    @WithMockUser(roles = "TRAINEE")
    @DisplayName("setActive: blocks trainee role (403)")
    void setActive_blocksTrainee() throws Exception {
        mockMvc.perform(patch("/trainers/Alice.Smith")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"isActive":false}
                                """))
                .andExpect(status().isForbidden());
        verify(trainerService, never()).deactivate(any());
    }
}

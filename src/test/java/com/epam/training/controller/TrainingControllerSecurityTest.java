package com.epam.training.controller;

import com.epam.training.security.UserDetailsServiceImpl;
import com.epam.training.service.TrainingService;
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
@DisplayName("TrainingController — security")
class TrainingControllerSecurityTest {

    @MockBean private TrainingService trainingService;
    @MockBean private UserDetailsServiceImpl userDetailsService;

    @Autowired private MockMvc mockMvc;

    private static final String TRAINING_JSON = """
            {
              "trainee": "John.Doe",
              "trainer": "Alice.Smith",
              "name":    "Morning Yoga",
              "date":    "2025-06-01",
              "duration": 60
            }
            """;

    // --- POST /trainings ---

    @Test
    @WithMockUser(roles = "TRAINEE", username = "John.Doe")
    @DisplayName("create: allows trainee who is the session's trainee")
    void create_allowsTraineeParticipant() throws Exception {
        mockMvc.perform(post("/trainings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TRAINING_JSON));
        verify(trainingService).create(any());
    }

    @Test
    @WithMockUser(roles = "TRAINER", username = "Alice.Smith")
    @DisplayName("create: allows trainer who is the session's trainer")
    void create_allowsTrainerParticipant() throws Exception {
        mockMvc.perform(post("/trainings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TRAINING_JSON));
        verify(trainingService).create(any());
    }

    @Test
    @WithMockUser(roles = "TRAINEE", username = "Other.User")
    @DisplayName("create: blocks trainee who is not a participant (403)")
    void create_blocksNonParticipant() throws Exception {
        mockMvc.perform(post("/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TRAINING_JSON))
                .andExpect(status().isForbidden());
        verify(trainingService, never()).create(any());
    }

    // --- GET /trainings/trainee/{username} ---

    @Test
    @WithMockUser(roles = "TRAINEE", username = "John.Doe")
    @DisplayName("getByTrainee: allows trainee owner")
    void getByTrainee_allowsOwner() throws Exception {
        when(trainingService.findByTrainee(any(), any(), any(), any(), any(), any()))
                .thenReturn(Page.empty());
        mockMvc.perform(get("/trainings/trainee/John.Doe"));
        verify(trainingService).findByTrainee(eq("John.Doe"), any(), any(), any(), any(), any());
    }

    @Test
    @WithMockUser(roles = "TRAINER", username = "Alice.Smith")
    @DisplayName("getByTrainee: blocks trainer role (403)")
    void getByTrainee_blocksTrainer() throws Exception {
        mockMvc.perform(get("/trainings/trainee/John.Doe"))
                .andExpect(status().isForbidden());
        verify(trainingService, never()).findByTrainee(any(), any(), any(), any(), any(), any());
    }

    @Test
    @WithMockUser(roles = "TRAINEE", username = "Other.User")
    @DisplayName("getByTrainee: blocks trainee with different username (403)")
    void getByTrainee_blocksOtherTrainee() throws Exception {
        mockMvc.perform(get("/trainings/trainee/John.Doe"))
                .andExpect(status().isForbidden());
        verify(trainingService, never()).findByTrainee(any(), any(), any(), any(), any(), any());
    }

    // --- GET /trainings/trainer/{username} ---

    @Test
    @WithMockUser(roles = "TRAINER", username = "Alice.Smith")
    @DisplayName("getByTrainer: allows trainer owner")
    void getByTrainer_allowsOwner() throws Exception {
        when(trainingService.findByTrainer(any(), any(), any(), any(), any()))
                .thenReturn(Page.empty());
        mockMvc.perform(get("/trainings/trainer/Alice.Smith"));
        verify(trainingService).findByTrainer(eq("Alice.Smith"), any(), any(), any(), any());
    }

    @Test
    @WithMockUser(roles = "TRAINEE", username = "John.Doe")
    @DisplayName("getByTrainer: blocks trainee role (403)")
    void getByTrainer_blocksTrainee() throws Exception {
        mockMvc.perform(get("/trainings/trainer/Alice.Smith"))
                .andExpect(status().isForbidden());
        verify(trainingService, never()).findByTrainer(any(), any(), any(), any(), any());
    }
}

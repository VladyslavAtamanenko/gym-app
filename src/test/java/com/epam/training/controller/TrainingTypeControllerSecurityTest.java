package com.epam.training.controller;

import com.epam.training.security.UserDetailsServiceImpl;
import com.epam.training.service.TrainingTypeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
@DisplayName("TrainingTypeController — security")
class TrainingTypeControllerSecurityTest {

    @MockBean private TrainingTypeService trainingTypeService;
    @MockBean private UserDetailsServiceImpl userDetailsService;

    @Autowired private MockMvc mockMvc;

    @Test
    @WithMockUser(roles = "TRAINEE")
    @DisplayName("getAll: allows trainee")
    void getAll_allowsTrainee() throws Exception {
        when(trainingTypeService.findAll()).thenReturn(List.of());
        mockMvc.perform(get("/training-types"))
                .andExpect(status().isOk());
        verify(trainingTypeService).findAll();
    }

    @Test
    @WithMockUser(roles = "TRAINER")
    @DisplayName("getAll: allows trainer")
    void getAll_allowsTrainer() throws Exception {
        when(trainingTypeService.findAll()).thenReturn(List.of());
        mockMvc.perform(get("/training-types"))
                .andExpect(status().isOk());
        verify(trainingTypeService).findAll();
    }

    @Test
    @DisplayName("getAll: blocks unauthenticated request (401)")
    void getAll_blocksAnonymous() throws Exception {
        mockMvc.perform(get("/training-types"))
                .andExpect(status().isUnauthorized());
    }
}

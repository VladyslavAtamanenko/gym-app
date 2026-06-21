package com.epam.training.controller;

import com.epam.training.dto.TrainingTypeDTO;
import com.epam.training.exception.handler.GlobalExceptionHandler;
import com.epam.training.service.TrainingTypeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TrainingTypeController")
class TrainingTypeControllerTest {

    @Mock
    private TrainingTypeService trainingTypeService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new TrainingTypeController(trainingTypeService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setMessageConverters(new MappingJackson2HttpMessageConverter(new ObjectMapper()))
                .build();
    }

    @Test
    @DisplayName("getAll: returns 200 with list of training types")
    void getAll_returnsOk() throws Exception {
        when(trainingTypeService.findAll()).thenReturn(List.of(
                new TrainingTypeDTO(1L, "Yoga"),
                new TrainingTypeDTO(2L, "Fitness")));

        mockMvc.perform(get("/training-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Yoga"))
                .andExpect(jsonPath("$[1].name").value("Fitness"));
    }

    @Test
    @DisplayName("getAll: returns 200 with empty list when no types exist")
    void getAll_returnsEmptyList() throws Exception {
        when(trainingTypeService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/training-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}

package com.epam.training.controller;

import com.epam.training.dto.*;
import com.epam.training.exception.TraineeNotFoundException;
import com.epam.training.exception.handler.GlobalExceptionHandler;
import com.epam.training.service.TraineeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TraineeController")
class TraineeControllerTest {

    @Mock
    private TraineeService traineeService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(new TraineeController(traineeService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }


    // --- PUT /trainees/{username}/password ---

    @Test
    @DisplayName("changePassword: returns 200 when old password matches")
    void changePassword_returnsOkOnSuccess() throws Exception {
        ChangeLoginRequest req = new ChangeLoginRequest("John.Doe", "oldPass", "newPass");
        when(traineeService.changePassword(any())).thenReturn(true);

        mockMvc.perform(put("/trainees/John.Doe/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("changePassword: returns 401 when old password does not match")
    void changePassword_returnsUnauthorizedOnMismatch() throws Exception {
        ChangeLoginRequest req = new ChangeLoginRequest("John.Doe", "wrongOld", "newPass");
        when(traineeService.changePassword(any())).thenReturn(false);

        mockMvc.perform(put("/trainees/John.Doe/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("changePassword: returns 400 when any field is blank")
    void changePassword_rejectsBlankField() throws Exception {
        ChangeLoginRequest req = new ChangeLoginRequest("John.Doe", "", "newPass");

        mockMvc.perform(put("/trainees/John.Doe/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());

        verify(traineeService, never()).changePassword(any());
    }

    // --- POST /trainees ---

    @Test
    @DisplayName("register: returns 201 with credentials on valid request")
    void register_returnsCreated() throws Exception {
        TraineeCreateRequest req = new TraineeCreateRequest("John", "Doe", null, null);
        TraineeCreateResponse resp = new TraineeCreateResponse("John.Doe", "pass123");
        when(traineeService.create(any())).thenReturn(resp);

        mockMvc.perform(post("/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("John.Doe"))
                .andExpect(jsonPath("$.password").value("pass123"));
    }

    @Test
    @DisplayName("register: returns 400 when firstName is blank")
    void register_rejectsBlankFirstName() throws Exception {
        TraineeCreateRequest req = new TraineeCreateRequest("", "Doe", null, null);

        mockMvc.perform(post("/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());

        verify(traineeService, never()).create(any());
    }

    @Test
    @DisplayName("register: returns 400 when lastName is blank")
    void register_rejectsBlankLastName() throws Exception {
        TraineeCreateRequest req = new TraineeCreateRequest("John", "", null, null);

        mockMvc.perform(post("/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    // --- GET /trainees/{username} ---

    @Test
    @DisplayName("getByUsername: returns 200 with trainee data")
    void getByUsername_returnsOk() throws Exception {
        TraineeGetResponse resp = new TraineeGetResponse("John", "Doe",
                LocalDate.of(1990, 1, 1), "123 St", true, List.of());
        when(traineeService.findByUsername("John.Doe")).thenReturn(resp);

        mockMvc.perform(get("/trainees/John.Doe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.isActive").value(true));
    }

    @Test
    @DisplayName("getByUsername: returns 404 when trainee not found")
    void getByUsername_returnsNotFound() throws Exception {
        when(traineeService.findByUsername("Ghost")).thenThrow(new TraineeNotFoundException("Ghost"));

        mockMvc.perform(get("/trainees/Ghost"))
                .andExpect(status().isNotFound());
    }

    // --- PUT /trainees/{username} ---

    @Test
    @DisplayName("update: returns 200 with updated data on valid request")
    void update_returnsOk() throws Exception {
        TraineeUpdateRequest req = new TraineeUpdateRequest("Jane", "Doe", null, null, true);
        TraineeUpdateResponse resp = new TraineeUpdateResponse();
        resp.setFirstName("Jane");
        when(traineeService.update(eq("John.Doe"), any())).thenReturn(resp);

        mockMvc.perform(put("/trainees/John.Doe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jane"));
    }

    @Test
    @DisplayName("update: returns 400 when firstName is blank")
    void update_rejectsBlankFirstName() throws Exception {
        TraineeUpdateRequest req = new TraineeUpdateRequest("", "Doe", null, null, true);

        mockMvc.perform(put("/trainees/John.Doe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("update: returns 400 when isActive is null")
    void update_rejectsNullIsActive() throws Exception {
        TraineeUpdateRequest req = new TraineeUpdateRequest("John", "Doe", null, null, null);

        mockMvc.perform(put("/trainees/John.Doe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    // --- PUT /trainees/{username}/trainers ---

    @Test
    @DisplayName("updateTrainersList: returns 200 with updated trainer list")
    void updateTrainersList_returnsOk() throws Exception {
        TraineeUpdateTrainersRequest req = new TraineeUpdateTrainersRequest(List.of("Trainer.One"));
        TrainerDTO dto = new TrainerDTO("Trainer.One", "Alice", "Smith", "Yoga");
        when(traineeService.updateTrainersList(eq("John.Doe"), any())).thenReturn(List.of(dto));

        mockMvc.perform(put("/trainees/John.Doe/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("Trainer.One"));
    }

    @Test
    @DisplayName("updateTrainersList: returns 400 when trainers list is empty")
    void updateTrainersList_rejectsEmptyList() throws Exception {
        TraineeUpdateTrainersRequest req = new TraineeUpdateTrainersRequest(List.of());

        mockMvc.perform(put("/trainees/John.Doe/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    // --- DELETE /trainees/{username} ---

    @Test
    @DisplayName("delete: returns 200 on success")
    void delete_returnsOk() throws Exception {
        mockMvc.perform(delete("/trainees/John.Doe"))
                .andExpect(status().isOk());

        verify(traineeService).delete("John.Doe");
    }

    @Test
    @DisplayName("delete: returns 404 when trainee not found")
    void delete_returnsNotFound() throws Exception {
        doThrow(new TraineeNotFoundException("Ghost")).when(traineeService).delete("Ghost");

        mockMvc.perform(delete("/trainees/Ghost"))
                .andExpect(status().isNotFound());
    }

    // --- PATCH /trainees/{username} ---

    @Test
    @DisplayName("setActive: returns 200 and calls activate when isActive is true")
    void setActive_callsActivate() throws Exception {
        ActivateRequest req = new ActivateRequest(true);

        mockMvc.perform(patch("/trainees/John.Doe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());

        verify(traineeService).activate("John.Doe");
        verify(traineeService, never()).deactivate(any());
    }

    @Test
    @DisplayName("setActive: returns 200 and calls deactivate when isActive is false")
    void setActive_callsDeactivate() throws Exception {
        ActivateRequest req = new ActivateRequest(false);

        mockMvc.perform(patch("/trainees/John.Doe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());

        verify(traineeService).deactivate("John.Doe");
        verify(traineeService, never()).activate(any());
    }

    @Test
    @DisplayName("setActive: returns 400 when isActive is null")
    void setActive_rejectsNullIsActive() throws Exception {
        ActivateRequest req = new ActivateRequest(null);

        mockMvc.perform(patch("/trainees/John.Doe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }
}

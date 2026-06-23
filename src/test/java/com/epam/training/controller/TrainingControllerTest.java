package com.epam.training.controller;

import com.epam.training.dto.GetTrainingsByTraineeResponse;
import com.epam.training.dto.GetTrainingsByTrainerResponse;
import com.epam.training.dto.TrainingCreateRequest;
import com.epam.training.exception.TraineeNotFoundException;
import com.epam.training.exception.TrainerNotFoundException;
import com.epam.training.exception.handler.GlobalExceptionHandler;
import com.epam.training.service.TrainingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.HateoasPageableHandlerMethodArgumentResolver;
import org.springframework.data.web.PagedResourcesAssemblerArgumentResolver;
import org.springframework.hateoas.mediatype.MessageResolver;
import org.springframework.hateoas.mediatype.hal.CurieProvider;
import org.springframework.hateoas.mediatype.hal.Jackson2HalModule;
import org.springframework.hateoas.server.core.AnnotationLinkRelationProvider;
import org.springframework.hateoas.server.core.DelegatingLinkRelationProvider;
import org.springframework.hateoas.server.core.EvoInflectorLinkRelationProvider;
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
@DisplayName("TrainingController")
class TrainingControllerTest {

    @Mock
    private TrainingService trainingService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.registerModule(new Jackson2HalModule());
        objectMapper.setHandlerInstantiator(new Jackson2HalModule.HalHandlerInstantiator(
                new DelegatingLinkRelationProvider(
                        new AnnotationLinkRelationProvider(),
                        new EvoInflectorLinkRelationProvider()),
                CurieProvider.NONE,
                MessageResolver.DEFAULTS_ONLY));

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(new TrainingController(trainingService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .setCustomArgumentResolvers(new PagedResourcesAssemblerArgumentResolver(new HateoasPageableHandlerMethodArgumentResolver()))
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    // --- POST /trainings ---

    @Test
    @DisplayName("create: returns 200 on valid request")
    void create_returnsOk() throws Exception {
        TrainingCreateRequest req = new TrainingCreateRequest(
                "John.Doe", "Alice.Smith", "Morning Yoga", "Yoga",
                LocalDate.of(2025, 1, 10), 60);
        when(trainingService.create(any())).thenReturn(true);

        mockMvc.perform(post("/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());

        verify(trainingService).create(any());
    }

    @Test
    @DisplayName("create: returns 400 when trainee is blank")
    void create_rejectsBlankTrainee() throws Exception {
        TrainingCreateRequest req = new TrainingCreateRequest(
                "", "Alice.Smith", "Morning Yoga", "Yoga",
                LocalDate.of(2025, 1, 10), 60);

        mockMvc.perform(post("/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());

        verify(trainingService, never()).create(any());
    }

    @Test
    @DisplayName("create: returns 400 when date is null")
    void create_rejectsNullDate() throws Exception {
        TrainingCreateRequest req = new TrainingCreateRequest(
                "John.Doe", "Alice.Smith", "Morning Yoga", "Yoga", null, 60);

        mockMvc.perform(post("/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("create: returns 400 when duration is not positive")
    void create_rejectsNonPositiveDuration() throws Exception {
        TrainingCreateRequest req = new TrainingCreateRequest(
                "John.Doe", "Alice.Smith", "Morning Yoga", "Yoga",
                LocalDate.of(2025, 1, 10), 0);

        mockMvc.perform(post("/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("create: returns 404 when trainee not found")
    void create_returnsNotFoundWhenTraineeMissing() throws Exception {
        TrainingCreateRequest req = new TrainingCreateRequest(
                "Ghost", "Alice.Smith", "Morning Yoga", "Yoga",
                LocalDate.of(2025, 1, 10), 60);
        when(trainingService.create(any())).thenThrow(new TraineeNotFoundException("Ghost"));

        mockMvc.perform(post("/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("create: returns 404 when trainer not found")
    void create_returnsNotFoundWhenTrainerMissing() throws Exception {
        TrainingCreateRequest req = new TrainingCreateRequest(
                "John.Doe", "Ghost", "Morning Yoga", "Yoga",
                LocalDate.of(2025, 1, 10), 60);
        when(trainingService.create(any())).thenThrow(new TrainerNotFoundException("Ghost"));

        mockMvc.perform(post("/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound());
    }

    // --- GET /trainings/trainee/{username} ---

    @Test
    @DisplayName("getByTrainee: returns 200 with paged training list")
    void getByTrainee_returnsPagedResult() throws Exception {
        var dto = new GetTrainingsByTraineeResponse("Morning Yoga", "Yoga",
                LocalDate.of(2025, 1, 10), 60, "Alice.Smith");
        PageImpl<GetTrainingsByTraineeResponse> page = new PageImpl<>(List.of(dto), PageRequest.of(0, 10), 1);
        when(trainingService.findByTrainee(eq("John.Doe"), any(), any(), any(), any(), any()))
                .thenReturn(page);

        mockMvc.perform(get("/trainings/trainee/John.Doe")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements").value(1));
    }

    @Test
    @DisplayName("getByTrainee: passes optional filters to service")
    void getByTrainee_passesFilters() throws Exception {
        PageImpl<GetTrainingsByTraineeResponse> page = new PageImpl<>(List.of(), PageRequest.of(0, 5), 0);
        when(trainingService.findByTrainee(any(), any(), any(), any(), any(), any())).thenReturn(page);

        mockMvc.perform(get("/trainings/trainee/John.Doe")
                        .param("from", "2025-01-01")
                        .param("to", "2025-12-31")
                        .param("trainerName", "Alice.Smith")
                        .param("trainingType", "Yoga")
                        .param("page", "1")
                        .param("size", "5"))
                .andExpect(status().isOk());

        verify(trainingService).findByTrainee(
                eq("John.Doe"),
                eq(LocalDate.of(2025, 1, 1)),
                eq(LocalDate.of(2025, 12, 31)),
                eq("Alice.Smith"),
                eq("Yoga"),
                eq(PageRequest.of(0, 5)));
    }

    @Test
    @DisplayName("getByTrainee: returns 400 when page is less than 1")
    void getByTrainee_rejectsZeroPage() throws Exception {
        mockMvc.perform(get("/trainings/trainee/John.Doe").param("page", "0"))
                .andExpect(status().isBadRequest());
    }

    // --- GET /trainings/trainer/{username} ---

    @Test
    @DisplayName("getByTrainer: returns 200 with paged training list")
    void getByTrainer_returnsPagedResult() throws Exception {
        var dto = new GetTrainingsByTrainerResponse("Morning Yoga", "Yoga",
                LocalDate.of(2025, 1, 10), 60, "John.Doe");
        PageImpl<GetTrainingsByTrainerResponse> page = new PageImpl<>(List.of(dto), PageRequest.of(0, 10), 1);
        when(trainingService.findByTrainer(eq("Alice.Smith"), any(), any(), any(), any()))
                .thenReturn(page);

        mockMvc.perform(get("/trainings/trainer/Alice.Smith")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements").value(1));
    }

    @Test
    @DisplayName("getByTrainer: passes optional filters to service")
    void getByTrainer_passesFilters() throws Exception {
        PageImpl<GetTrainingsByTrainerResponse> page = new PageImpl<>(List.of(), PageRequest.of(0, 5), 0);
        when(trainingService.findByTrainer(any(), any(), any(), any(), any())).thenReturn(page);

        mockMvc.perform(get("/trainings/trainer/Alice.Smith")
                        .param("from", "2025-01-01")
                        .param("to", "2025-06-30")
                        .param("traineeName", "John.Doe")
                        .param("page", "1")
                        .param("size", "5"))
                .andExpect(status().isOk());

        verify(trainingService).findByTrainer(
                eq("Alice.Smith"),
                eq(LocalDate.of(2025, 1, 1)),
                eq(LocalDate.of(2025, 6, 30)),
                eq("John.Doe"),
                eq(PageRequest.of(0, 5)));
    }

    @Test
    @DisplayName("getByTrainer: returns 400 when page is less than 1")
    void getByTrainer_rejectsZeroPage() throws Exception {
        mockMvc.perform(get("/trainings/trainer/Alice.Smith").param("page", "0"))
                .andExpect(status().isBadRequest());
    }
}

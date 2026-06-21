package com.epam.training.controller;

import com.epam.training.dto.*;
import com.epam.training.exception.TrainerNotFoundException;
import com.epam.training.exception.handler.GlobalExceptionHandler;
import com.epam.training.service.TrainerService;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TrainerController")
class TrainerControllerTest {

    @Mock
    private TrainerService trainerService;

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
                .standaloneSetup(new TrainerController(trainerService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .setCustomArgumentResolvers(new PagedResourcesAssemblerArgumentResolver(new HateoasPageableHandlerMethodArgumentResolver()))
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    // --- POST /trainers ---

    @Test
    @DisplayName("register: returns 201 with credentials on valid request")
    void register_returnsCreated() throws Exception {
        TrainerCreateRequest req = new TrainerCreateRequest("Alice", "Smith", "Yoga");
        TrainerCreateResponse resp = new TrainerCreateResponse("Alice.Smith", "pass456");
        when(trainerService.create(any())).thenReturn(resp);

        mockMvc.perform(post("/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("Alice.Smith"))
                .andExpect(jsonPath("$.password").value("pass456"));
    }

    @Test
    @DisplayName("register: returns 400 when firstName is blank")
    void register_rejectsBlankFirstName() throws Exception {
        TrainerCreateRequest req = new TrainerCreateRequest("", "Smith", "Yoga");

        mockMvc.perform(post("/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());

        verify(trainerService, never()).create(any());
    }

    @Test
    @DisplayName("register: returns 400 when specialization is blank")
    void register_rejectsBlankSpecialization() throws Exception {
        TrainerCreateRequest req = new TrainerCreateRequest("Alice", "Smith", "");

        mockMvc.perform(post("/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    // --- GET /trainers/{username} ---

    @Test
    @DisplayName("getByUsername: returns 200 with trainer data")
    void getByUsername_returnsOk() throws Exception {
        TrainerGetResponse resp = new TrainerGetResponse("Alice", "Smith", "Yoga", true, List.of());
        when(trainerService.findByUsername("Alice.Smith")).thenReturn(resp);

        mockMvc.perform(get("/trainers/Alice.Smith"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Alice"))
                .andExpect(jsonPath("$.specialization").value("Yoga"))
                .andExpect(jsonPath("$.isActive").value(true));
    }

    @Test
    @DisplayName("getByUsername: returns 404 when trainer not found")
    void getByUsername_returnsNotFound() throws Exception {
        when(trainerService.findByUsername("Ghost")).thenThrow(new TrainerNotFoundException("Ghost"));

        mockMvc.perform(get("/trainers/Ghost"))
                .andExpect(status().isNotFound());
    }

    // --- PUT /trainers/{username} ---

    @Test
    @DisplayName("update: returns 200 with updated data on valid request")
    void update_returnsOk() throws Exception {
        TrainerUpdateRequest req = new TrainerUpdateRequest("Alice", "Johnson", "Yoga", true);
        TrainerUpdateResponse resp = new TrainerUpdateResponse();
        resp.setFirstName("Alice");
        resp.setLastName("Johnson");
        when(trainerService.update(eq("Alice.Smith"), any())).thenReturn(resp);

        mockMvc.perform(put("/trainers/Alice.Smith")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Alice"))
                .andExpect(jsonPath("$.lastName").value("Johnson"));
    }

    @Test
    @DisplayName("update: returns 400 when firstName is blank")
    void update_rejectsBlankFirstName() throws Exception {
        TrainerUpdateRequest req = new TrainerUpdateRequest("", "Smith", "Yoga", true);

        mockMvc.perform(put("/trainers/Alice.Smith")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("update: returns 400 when isActive is null")
    void update_rejectsNullIsActive() throws Exception {
        TrainerUpdateRequest req = new TrainerUpdateRequest("Alice", "Smith", "Yoga", null);

        mockMvc.perform(put("/trainers/Alice.Smith")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    // --- GET /trainers/not-assigned/{username} ---

    @Test
    @DisplayName("getNotAssignedOnTrainee: returns 200 with paged trainer list")
    void getNotAssignedOnTrainee_returnsPagedResult() throws Exception {
        TrainerDTO dto = new TrainerDTO("Trainer.One", "Bob", "Jones", "Fitness");
        PageImpl<TrainerDTO> page = new PageImpl<>(List.of(dto), PageRequest.of(0, 10), 1);
        when(trainerService.findNotAssignedOnTrainee(eq("John.Doe"), any())).thenReturn(page);

        mockMvc.perform(get("/trainers/not-assigned/John.Doe")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements").value(1))
                .andExpect(jsonPath("$.page.size").value(10));
    }

    @Test
    @DisplayName("getNotAssignedOnTrainee: returns 400 when page is less than 1")
    void getNotAssignedOnTrainee_rejectsZeroPage() throws Exception {
        mockMvc.perform(get("/trainers/not-assigned/John.Doe")
                        .param("page", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("getNotAssignedOnTrainee: uses defaults when page and size are omitted")
    void getNotAssignedOnTrainee_usesDefaults() throws Exception {
        PageImpl<TrainerDTO> page = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);
        when(trainerService.findNotAssignedOnTrainee(eq("John.Doe"), any())).thenReturn(page);

        mockMvc.perform(get("/trainers/not-assigned/John.Doe"))
                .andExpect(status().isOk());

        verify(trainerService).findNotAssignedOnTrainee(eq("John.Doe"), eq(PageRequest.of(0, 10)));
    }

    // --- PATCH /trainers/{username} ---

    @Test
    @DisplayName("setActive: calls activate when isActive is true")
    void setActive_callsActivate() throws Exception {
        ActivateRequest req = new ActivateRequest(true);

        mockMvc.perform(patch("/trainers/Alice.Smith")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());

        verify(trainerService).activate("Alice.Smith");
        verify(trainerService, never()).deactivate(any());
    }

    @Test
    @DisplayName("setActive: calls deactivate when isActive is false")
    void setActive_callsDeactivate() throws Exception {
        ActivateRequest req = new ActivateRequest(false);

        mockMvc.perform(patch("/trainers/Alice.Smith")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());

        verify(trainerService).deactivate("Alice.Smith");
        verify(trainerService, never()).activate(any());
    }

    @Test
    @DisplayName("setActive: returns 400 when isActive is null")
    void setActive_rejectsNullIsActive() throws Exception {
        ActivateRequest req = new ActivateRequest(null);

        mockMvc.perform(patch("/trainers/Alice.Smith")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }
}

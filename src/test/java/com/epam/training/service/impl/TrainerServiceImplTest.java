package com.epam.training.service.impl;

import com.epam.training.dao.TrainerDao;
import com.epam.training.dao.TrainingTypeDao;
import com.epam.training.dto.*;
import com.epam.training.mapper.ToDTOMapper;
import com.epam.training.mapper.ToEntityMapper;
import com.epam.training.model.Trainer;
import com.epam.training.model.TrainingType;
import com.epam.training.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.mockito.Mock;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TrainerService")
class TrainerServiceImplTest {

    @Mock private TrainerDao trainerDao;
    @Mock private TrainingTypeDao specializationDao;
    @Mock private ToEntityMapper<TrainerCreateRequest, Trainer> trainerCreateRequestMapper;
    @Mock private ToDTOMapper<Trainer, TrainerCreateResponse> trainerCreateResponseMapper;
    @Mock private ToDTOMapper<Trainer, TrainerGetResponse> trainerGetResponseMapper;
    @Mock private ToDTOMapper<Trainer, TrainerUpdateResponse> trainerUpdateResponseMapper;
    @Mock private ToDTOMapper<Trainer, TrainerDTO> trainerMapper;
    @Mock private UserUtil userUtil;
    @Mock private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    private TrainerServiceImpl trainerService;

    private Trainer trainer;
    private User user;
    private TrainingType yoga;

    @BeforeEach
    void setUp() {
        trainerService = new TrainerServiceImpl(
                trainerDao, specializationDao,
                trainerCreateRequestMapper, trainerCreateResponseMapper,
                trainerGetResponseMapper, trainerUpdateResponseMapper,
                trainerMapper, userUtil, passwordEncoder, new SimpleMeterRegistry());

        yoga = TrainingType.builder().id(1L).name("Yoga").build();
        user = User.builder()
                .id(2L)
                .firstName("Jane")
                .lastName("Smith")
                .username("Jane.Smith")
                .password("secret")
                .isActive(true)
                .build();
        trainer = Trainer.builder()
                .id(20L)
                .user(user)
                .specialization(yoga)
                .build();
    }

    @Test
    @DisplayName("create: initializes user and saves when request is valid")
    void create_initializesUserAndSaves() {
        TrainerCreateRequest request = new TrainerCreateRequest("Jane", "Smith", "Yoga");
        TrainerCreateResponse response = new TrainerCreateResponse("Jane.Smith", "pass", null);
        when(trainerCreateRequestMapper.toEntity(request)).thenReturn(trainer);
        when(userUtil.initializeUser(user)).thenReturn("generatedP");
        when(trainerDao.save(trainer)).thenReturn(trainer);
        when(trainerCreateResponseMapper.toDTO(trainer)).thenReturn(response);

        assertEquals(response, trainerService.create(request));

        verify(userUtil).initializeUser(user);
    }

    @Test
    @DisplayName("create: throws IllegalArgumentException when request is null")
    void create_rejectsNullRequest() {
        assertThrows(IllegalArgumentException.class, () -> trainerService.create(null));
    }

    @Test
    @DisplayName("create: throws IllegalArgumentException when required fields are blank")
    void create_rejectsMissingFields() {
        TrainerCreateRequest request = new TrainerCreateRequest("", "Smith", "Yoga");
        assertThrows(IllegalArgumentException.class, () -> trainerService.create(request));
    }

    @Test
    @DisplayName("credentialsMatch: returns true when password matches")
    void credentialsMatch_returnsTrueForValidPassword() {
        LoginRequest login = new LoginRequest("Jane.Smith", "secret");
        when(trainerDao.findByUsername("Jane.Smith")).thenReturn(Optional.of(trainer));
        when(passwordEncoder.matches("secret", "secret")).thenReturn(true);

        assertTrue(trainerService.credentialsMatch(login));
    }

    @Test
    @DisplayName("credentialsMatch: returns false when password does not match")
    void credentialsMatch_returnsFalseForInvalidPassword() {
        LoginRequest login = new LoginRequest("Jane.Smith", "wrong");
        when(trainerDao.findByUsername("Jane.Smith")).thenReturn(Optional.of(trainer));

        assertFalse(trainerService.credentialsMatch(login));
    }

    @Test
    @DisplayName("credentialsMatch: returns false when trainer not found")
    void credentialsMatch_returnsFalseWhenUserNotFound() {
        LoginRequest login = new LoginRequest("missing", "secret");
        when(trainerDao.findByUsername("missing")).thenReturn(Optional.empty());

        assertFalse(trainerService.credentialsMatch(login));
    }

    @Test
    @DisplayName("credentialsMatch: throws IllegalArgumentException when request is null")
    void credentialsMatch_rejectsNullRequest() {
        assertThrows(IllegalArgumentException.class, () -> trainerService.credentialsMatch(null));
    }

    @Test
    @DisplayName("changePassword: updates password when old password matches")
    void changePassword_updatesWhenOldPasswordMatches() {
        ChangeLoginRequest request = new ChangeLoginRequest("Jane.Smith", "secret", "newpass");
        when(trainerDao.findByUsername("Jane.Smith")).thenReturn(Optional.of(trainer));
        when(passwordEncoder.matches("secret", "secret")).thenReturn(true);
        when(passwordEncoder.encode("newpass")).thenReturn("encoded_newpass");

        assertTrue(trainerService.changePassword(request));
        assertEquals("encoded_newpass", user.getPassword());
    }

    @Test
    @DisplayName("changePassword: returns false when old password does not match")
    void changePassword_returnsFalseWhenOldPasswordDoesNotMatch() {
        ChangeLoginRequest request = new ChangeLoginRequest("Jane.Smith", "wrong", "newpass");
        when(trainerDao.findByUsername("Jane.Smith")).thenReturn(Optional.of(trainer));

        assertFalse(trainerService.changePassword(request));
        assertEquals("secret", user.getPassword());
    }

    @Test
    @DisplayName("update: applies isActive from request; specialization is read-only and not changed")
    void update_appliesIsActiveAndSpecialization() {
        TrainerUpdateRequest request = new TrainerUpdateRequest("Jane", "Smith", "Yoga", false);
        TrainerUpdateResponse response = new TrainerUpdateResponse();

        when(trainerDao.findByUsername("Jane.Smith")).thenReturn(Optional.of(trainer));
        when(userUtil.updateUser(eq(user), any(User.class))).thenAnswer(invocation -> invocation.getArgument(1));
        when(trainerDao.save(trainer)).thenReturn(trainer);
        when(trainerUpdateResponseMapper.toDTO(trainer)).thenReturn(response);

        assertEquals(response, trainerService.update("Jane.Smith", request));

        verify(userUtil).updateUser(eq(user), argThat(u -> Boolean.FALSE.equals(u.getIsActive())));
        verifyNoInteractions(specializationDao);
    }

    @Test
    @DisplayName("update: throws IllegalArgumentException when required fields are missing")
    void update_rejectsMissingRequiredFields() {
        TrainerUpdateRequest request = new TrainerUpdateRequest("", "Smith", "Yoga", true);
        assertThrows(IllegalArgumentException.class, () -> trainerService.update("Jane.Smith", request));
    }

    @Test
    @DisplayName("activate: sets isActive to true and saves")
    void activate_setsActiveTrue() {
        user.setIsActive(false);
        TrainerGetResponse response = new TrainerGetResponse();
        when(trainerDao.findByUsername("Jane.Smith")).thenReturn(Optional.of(trainer));
        when(trainerDao.save(trainer)).thenReturn(trainer);
        when(trainerGetResponseMapper.toDTO(trainer)).thenReturn(response);

        assertEquals(response, trainerService.activate("Jane.Smith"));
        assertTrue(user.getIsActive());
    }

    @Test
    @DisplayName("activate: throws IllegalStateException when trainer is already active")
    void activate_rejectsAlreadyActive() {
        when(trainerDao.findByUsername("Jane.Smith")).thenReturn(Optional.of(trainer));

        assertThrows(IllegalStateException.class, () -> trainerService.activate("Jane.Smith"));
        verify(trainerDao, never()).save(any());
    }

    @Test
    @DisplayName("deactivate: sets isActive to false and saves")
    void deactivate_setsActiveFalse() {
        TrainerGetResponse response = new TrainerGetResponse();
        when(trainerDao.findByUsername("Jane.Smith")).thenReturn(Optional.of(trainer));
        when(trainerDao.save(trainer)).thenReturn(trainer);
        when(trainerGetResponseMapper.toDTO(trainer)).thenReturn(response);

        assertEquals(response, trainerService.deactivate("Jane.Smith"));
        assertFalse(user.getIsActive());
    }

    @Test
    @DisplayName("deactivate: throws IllegalStateException when trainer is already inactive")
    void deactivate_rejectsAlreadyInactive() {
        user.setIsActive(false);
        when(trainerDao.findByUsername("Jane.Smith")).thenReturn(Optional.of(trainer));

        assertThrows(IllegalStateException.class, () -> trainerService.deactivate("Jane.Smith"));
    }

    @Test
    @DisplayName("findByUsername: returns mapped DTO when trainer exists")
    void findByUsername_returnsMappedResult() {
        TrainerGetResponse response = new TrainerGetResponse();
        when(trainerDao.findByUsername("Jane.Smith")).thenReturn(Optional.of(trainer));
        when(trainerGetResponseMapper.toDTO(trainer)).thenReturn(response);

        assertEquals(response, trainerService.findByUsername("Jane.Smith"));
    }

    @Test
    @DisplayName("findNotAssignedOnTrainee: returns trainers not assigned to given trainee")
    void findNotAssignedOnTrainee_delegatesToDao() {
        var pageable = PageRequest.of(0, 10);
        TrainerDTO dto = new TrainerDTO();
        when(trainerDao.findNotAssignedOnTrainee("John.Doe", pageable))
                .thenReturn(new PageImpl<>(List.of(trainer), pageable, 1));
        when(trainerMapper.toDTO(trainer)).thenReturn(dto);

        assertEquals(List.of(dto), trainerService.findNotAssignedOnTrainee("John.Doe", pageable).getContent());
    }

    @Test
    @DisplayName("findAll: returns mapped list of all trainers")
    void findAll_returnsMappedList() {
        TrainerGetResponse response = new TrainerGetResponse();
        when(trainerDao.findAll()).thenReturn(List.of(trainer));
        when(trainerGetResponseMapper.toDTO(trainer)).thenReturn(response);

        assertEquals(List.of(response), trainerService.findAll());
    }
}

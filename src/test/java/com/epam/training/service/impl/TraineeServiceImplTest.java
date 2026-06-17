package com.epam.training.service.impl;

import com.epam.training.dao.TraineeDao;
import com.epam.training.dao.TrainerDao;
import com.epam.training.dto.*;
import com.epam.training.mapper.ToDTOMapper;
import com.epam.training.mapper.ToEntityMapper;
import com.epam.training.model.Trainee;
import com.epam.training.model.Trainer;
import com.epam.training.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TraineeService")
class TraineeServiceImplTest {

    @Mock private TraineeDao traineeDao;
    @Mock private TrainerDao trainerDao;
    @Mock private ToEntityMapper<TraineeCreateRequest, Trainee> traineeCreateRequestMapper;
    @Mock private ToDTOMapper<Trainee, TraineeCreateResponse> traineeCreateResponseMapper;
    @Mock private ToDTOMapper<Trainee, TraineeUpdateResponse> traineeUpdateResponseMapper;
    @Mock private ToDTOMapper<Trainee, TraineeGetResponse> traineeGetResponseMapper;
    @Mock private ToDTOMapper<Trainer, TrainerDTO> trainerMapper;
    @Mock private UserUtil userUtil;

    @InjectMocks
    private TraineeServiceImpl traineeService;

    private Trainee trainee;
    private User user;

    @BeforeEach
    void setUp() {
        traineeService.setTraineeCreateRequestMapper(traineeCreateRequestMapper);
        traineeService.setTraineeCreateResponseMapper(traineeCreateResponseMapper);
        traineeService.setTraineeUpdateResponseMapper(traineeUpdateResponseMapper);
        traineeService.setTraineeGetResponseMapper(traineeGetResponseMapper);
        traineeService.setTrainerMapper(trainerMapper);
        traineeService.setUserUtil(userUtil);

        user = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .username("John.Doe")
                .password("secret")
                .isActive(true)
                .build();
        trainee = Trainee.builder()
                .id(10L)
                .address("Street")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .user(user)
                .trainers(new ArrayList<>())
                .build();
    }

    @Test
    @DisplayName("create: initializes user and saves when request is valid")
    void create_initializesUserAndSaves() {
        TraineeCreateRequest request = new TraineeCreateRequest("John", "Doe", LocalDate.of(1990, 1, 1), "Street");
        TraineeCreateResponse response = new TraineeCreateResponse("John.Doe", "pass");
        when(traineeCreateRequestMapper.toEntity(request)).thenReturn(trainee);
        when(traineeDao.save(trainee)).thenReturn(trainee);
        when(traineeCreateResponseMapper.toDTO(trainee)).thenReturn(response);

        assertEquals(response, traineeService.create(request));

        verify(userUtil).initializeUser(user);
        verify(traineeDao).save(trainee);
    }

    @Test
    @DisplayName("create: throws IllegalArgumentException when request is null")
    void create_rejectsNullRequest() {
        assertThrows(IllegalArgumentException.class, () -> traineeService.create(null));
    }

    @Test
    @DisplayName("create: throws IllegalArgumentException when required fields are blank")
    void create_rejectsMissingRequiredFields() {
        TraineeCreateRequest request = new TraineeCreateRequest("", "Doe", LocalDate.of(1990, 1, 1), "Street");
        assertThrows(IllegalArgumentException.class, () -> traineeService.create(request));
    }

    @Test
    @DisplayName("credentialsMatch: returns true when password matches")
    void credentialsMatch_returnsTrueForValidPassword() {
        LoginRequest login = new LoginRequest("John.Doe", "secret");
        when(traineeDao.findByUsername("John.Doe")).thenReturn(Optional.of(trainee));

        assertTrue(traineeService.credentialsMatch(login));
    }

    @Test
    @DisplayName("credentialsMatch: returns false when password does not match")
    void credentialsMatch_returnsFalseForInvalidPassword() {
        LoginRequest login = new LoginRequest("John.Doe", "wrong");
        when(traineeDao.findByUsername("John.Doe")).thenReturn(Optional.of(trainee));

        assertFalse(traineeService.credentialsMatch(login));
    }

    @Test
    @DisplayName("credentialsMatch: returns false when trainee not found")
    void credentialsMatch_returnsFalseWhenUserNotFound() {
        LoginRequest login = new LoginRequest("missing", "secret");
        when(traineeDao.findByUsername("missing")).thenReturn(Optional.empty());

        assertFalse(traineeService.credentialsMatch(login));
    }

    @Test
    @DisplayName("credentialsMatch: throws IllegalArgumentException when request is null")
    void credentialsMatch_rejectsNullRequest() {
        assertThrows(IllegalArgumentException.class, () -> traineeService.credentialsMatch(null));
    }

    @Test
    @DisplayName("changePassword: updates password when old password matches")
    void changePassword_updatesWhenOldPasswordMatches() {
        ChangeLoginRequest request = new ChangeLoginRequest("John.Doe", "secret", "newpass");
        when(traineeDao.findByUsername("John.Doe")).thenReturn(Optional.of(trainee));

        assertTrue(traineeService.changePassword(request));
        assertEquals("newpass", user.getPassword());
    }

    @Test
    @DisplayName("changePassword: returns false when old password does not match")
    void changePassword_returnsFalseWhenOldPasswordDoesNotMatch() {
        ChangeLoginRequest request = new ChangeLoginRequest("John.Doe", "wrong", "newpass");
        when(traineeDao.findByUsername("John.Doe")).thenReturn(Optional.of(trainee));

        assertFalse(traineeService.changePassword(request));
        assertEquals("secret", user.getPassword());
    }

    @Test
    @DisplayName("update: throws IllegalArgumentException when required fields are missing")
    void update_rejectsMissingRequiredFields() {
        TraineeUpdateRequest request = new TraineeUpdateRequest(
                "", "John", "Doe", LocalDate.of(1990, 1, 1), "Street", true);
        assertThrows(IllegalArgumentException.class, () -> traineeService.update(request));
    }

    @Test
    @DisplayName("activate: sets isActive to true and saves")
    void activate_setsActiveTrue() {
        user.setIsActive(false);
        TraineeGetResponse response = new TraineeGetResponse();
        when(traineeDao.findByUsername("John.Doe")).thenReturn(Optional.of(trainee));
        when(traineeDao.save(trainee)).thenReturn(trainee);
        when(traineeGetResponseMapper.toDTO(trainee)).thenReturn(response);

        assertEquals(response, traineeService.activate("John.Doe"));
        assertTrue(user.getIsActive());
    }

    @Test
    @DisplayName("activate: throws IllegalStateException when trainee is already active")
    void activate_rejectsAlreadyActive() {
        when(traineeDao.findByUsername("John.Doe")).thenReturn(Optional.of(trainee));

        assertThrows(IllegalStateException.class, () -> traineeService.activate("John.Doe"));
        verify(traineeDao, never()).save(any());
    }

    @Test
    @DisplayName("deactivate: sets isActive to false and saves")
    void deactivate_setsActiveFalse() {
        TraineeGetResponse response = new TraineeGetResponse();
        when(traineeDao.findByUsername("John.Doe")).thenReturn(Optional.of(trainee));
        when(traineeDao.save(trainee)).thenReturn(trainee);
        when(traineeGetResponseMapper.toDTO(trainee)).thenReturn(response);

        assertEquals(response, traineeService.deactivate("John.Doe"));
        assertFalse(user.getIsActive());
    }

    @Test
    @DisplayName("deactivate: throws IllegalStateException when trainee is already inactive")
    void deactivate_rejectsAlreadyInactive() {
        user.setIsActive(false);
        when(traineeDao.findByUsername("John.Doe")).thenReturn(Optional.of(trainee));

        assertThrows(IllegalStateException.class, () -> traineeService.deactivate("John.Doe"));
    }

    @Test
    @DisplayName("delete: delegates deletion to DAO")
    void delete_delegatesToDao() {
        traineeService.delete("John.Doe");
        verify(traineeDao).delete("John.Doe");
    }

    @Test
    @DisplayName("findByUsername: returns mapped DTO when trainee exists")
    void findByUsername_returnsMappedResult() {
        TraineeGetResponse response = new TraineeGetResponse();
        when(traineeDao.findByUsername("John.Doe")).thenReturn(Optional.of(trainee));
        when(traineeGetResponseMapper.toDTO(trainee)).thenReturn(response);

        assertEquals(Optional.of(response), traineeService.findByUsername("John.Doe"));
    }

    @Test
    @DisplayName("findAll: returns mapped list of all trainees")
    void findAll_returnsMappedList() {
        TraineeGetResponse response = new TraineeGetResponse();
        when(traineeDao.findAll()).thenReturn(List.of(trainee));
        when(traineeGetResponseMapper.toDTO(trainee)).thenReturn(response);

        assertEquals(List.of(response), traineeService.findAll());
    }

    @Test
    @DisplayName("updateTrainersList: replaces trainers list with new trainers from request")
    void updateTrainersList_replacesTrainers() {
        Trainer trainer = new Trainer();
        trainer.setUser(User.builder().username("Trainer.One").build());
        TraineeUpdateTrainersRequest request = new TraineeUpdateTrainersRequest("John.Doe", List.of("Trainer.One"));
        TrainerDTO dto = new TrainerDTO();

        when(traineeDao.findByUsername("John.Doe")).thenReturn(Optional.of(trainee));
        when(trainerDao.findByUsername("Trainer.One")).thenReturn(Optional.of(trainer));
        when(trainerMapper.toDTO(trainer)).thenReturn(dto);

        assertEquals(List.of(dto), traineeService.updateTrainersList(request));
        assertEquals(1, trainee.getTrainers().size());
    }
}

package com.epam.training.facade;

import com.epam.training.dto.*;
import com.epam.training.service.TraineeService;
import com.epam.training.service.TrainerService;
import com.epam.training.service.TrainingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GymApp facade")
class GymAppTest {

    @Mock
    private TraineeService traineeService;
    @Mock
    private TrainerService trainerService;
    @Mock
    private TrainingService trainingService;

    @InjectMocks
    private GymApp gymApp;

    private LoginRequest traineeLogin;
    private LoginRequest trainerLogin;

    @BeforeEach
    void setUp() {
        traineeLogin = new LoginRequest("trainee.user", "pass");
        trainerLogin = new LoginRequest("trainer.user", "pass");
    }

    @Test
    @DisplayName("createTrainee: does not require authentication")
    void createTrainee_doesNotRequireLogin() {
        TraineeCreateRequest request = new TraineeCreateRequest();
        TraineeCreateResponse response = new TraineeCreateResponse("trainee.user", "pass");
        when(traineeService.create(request)).thenReturn(response);

        assertEquals(response, gymApp.createTrainee(request));

        verify(traineeService).create(request);
        verify(traineeService, never()).credentialsMatch(any());
    }

    @Test
    @DisplayName("updateTrainee: delegates to service after successful authentication")
    void updateTrainee_requiresLoginAndDelegates() {
        TraineeUpdateRequest request = new TraineeUpdateRequest();
        TraineeUpdateResponse response = new TraineeUpdateResponse();
        when(traineeService.credentialsMatch(traineeLogin)).thenReturn(true);
        when(traineeService.update(request)).thenReturn(response);

        assertEquals(response, gymApp.updateTrainee(traineeLogin, request));

        verify(traineeService).credentialsMatch(traineeLogin);
        verify(traineeService).update(request);
    }

    @Test
    @DisplayName("updateTrainee: throws SecurityException when credentials are invalid")
    void updateTrainee_rejectsInvalidLogin() {
        TraineeUpdateRequest request = new TraineeUpdateRequest();
        when(traineeService.credentialsMatch(traineeLogin)).thenReturn(false);

        assertThrows(SecurityException.class, () -> gymApp.updateTrainee(traineeLogin, request));

        verify(traineeService, never()).update(any());
    }

    @Test
    @DisplayName("createTrainer: does not require authentication")
    void createTrainer_doesNotRequireLogin() {
        TrainerCreateRequest request = new TrainerCreateRequest();
        TrainerCreateResponse response = new TrainerCreateResponse("trainer.user", "pass");
        when(trainerService.create(request)).thenReturn(response);

        assertEquals(response, gymApp.createTrainer(request));

        verify(trainerService).create(request);
        verify(trainerService, never()).credentialsMatch(any());
    }

    @Test
    @DisplayName("updateTrainer: delegates to service after successful authentication")
    void updateTrainer_requiresLoginAndDelegates() {
        TrainerUpdateRequest request = new TrainerUpdateRequest();
        TrainerUpdateResponse response = new TrainerUpdateResponse();
        when(trainerService.credentialsMatch(trainerLogin)).thenReturn(true);
        when(trainerService.update(request)).thenReturn(response);

        assertEquals(response, gymApp.updateTrainer(trainerLogin, request));

        verify(trainerService).credentialsMatch(trainerLogin);
        verify(trainerService).update(request);
    }

    @Test
    @DisplayName("updateTrainer: throws SecurityException when credentials are invalid")
    void updateTrainer_rejectsInvalidLogin() {
        TrainerUpdateRequest request = new TrainerUpdateRequest();
        when(trainerService.credentialsMatch(trainerLogin)).thenReturn(false);

        assertThrows(SecurityException.class, () -> gymApp.updateTrainer(trainerLogin, request));

        verify(trainerService, never()).update(any());
    }

    @Test
    @DisplayName("createTraining: requires trainer authentication before delegating")
    void createTraining_requiresTrainerLoginAndDelegates() {
        TrainingCreateRequest request = new TrainingCreateRequest();
        when(trainerService.credentialsMatch(trainerLogin)).thenReturn(true);
        when(trainingService.create(request)).thenReturn(true);

        assertTrue(gymApp.createTraining(trainerLogin, request));

        verify(trainerService).credentialsMatch(trainerLogin);
        verify(trainingService).create(request);
    }

    @Test
    @DisplayName("findTrainingsByTrainee: requires trainee authentication before delegating")
    void findTrainingsByTrainee_requiresTraineeLoginAndDelegates() {
        GetTrainingsByTraineeRequest request = new GetTrainingsByTraineeRequest();
        List<GetTrainingsByTraineeResponse> response = List.of(new GetTrainingsByTraineeResponse());
        when(traineeService.credentialsMatch(traineeLogin)).thenReturn(true);
        when(trainingService.findByTrainee(request)).thenReturn(response);

        assertEquals(response, gymApp.findTrainingsByTrainee(traineeLogin, request));

        verify(traineeService).credentialsMatch(traineeLogin);
        verify(trainingService).findByTrainee(request);
    }

    @Test
    @DisplayName("activateTrainee: requires trainee authentication then activates")
    void activateTrainee_requiresLoginAndDelegates() {
        TraineeGetResponse response = new TraineeGetResponse();
        when(traineeService.credentialsMatch(traineeLogin)).thenReturn(true);
        when(traineeService.activate("trainee.user")).thenReturn(response);

        assertEquals(response, gymApp.activateTrainee(traineeLogin, "trainee.user"));

        verify(traineeService).activate("trainee.user");
    }

    @Test
    @DisplayName("deactivateTrainee: throws SecurityException when credentials are invalid")
    void deactivateTrainee_rejectsInvalidLogin() {
        when(traineeService.credentialsMatch(traineeLogin)).thenReturn(false);

        assertThrows(SecurityException.class, () -> gymApp.deactivateTrainee(traineeLogin, "trainee.user"));

        verify(traineeService, never()).deactivate(any());
    }

    @Test
    @DisplayName("activateTrainer: requires trainer authentication then activates")
    void activateTrainer_requiresLoginAndDelegates() {
        TrainerGetResponse response = new TrainerGetResponse();
        when(trainerService.credentialsMatch(trainerLogin)).thenReturn(true);
        when(trainerService.activate("trainer.user")).thenReturn(response);

        assertEquals(response, gymApp.activateTrainer(trainerLogin, "trainer.user"));

        verify(trainerService).activate("trainer.user");
    }

    @Test
    @DisplayName("deactivateTrainer: requires trainer authentication then deactivates")
    void deactivateTrainer_requiresLoginAndDelegates() {
        TrainerGetResponse response = new TrainerGetResponse();
        when(trainerService.credentialsMatch(trainerLogin)).thenReturn(true);
        when(trainerService.deactivate("trainer.user")).thenReturn(response);

        assertEquals(response, gymApp.deactivateTrainer(trainerLogin, "trainer.user"));

        verify(trainerService).deactivate("trainer.user");
    }

    @Test
    @DisplayName("loginTrainee: delegates credential check to service")
    void loginTrainee_delegatesToService() {
        when(traineeService.credentialsMatch(traineeLogin)).thenReturn(true);
        assertTrue(gymApp.loginTrainee(traineeLogin));
    }

    @Test
    @DisplayName("changeTraineePassword: requires trainee authentication before changing password")
    void changeTraineePassword_requiresLogin() {
        ChangeLoginRequest change = new ChangeLoginRequest("trainee.user", "pass", "new");
        when(traineeService.credentialsMatch(traineeLogin)).thenReturn(true);
        when(traineeService.changePassword(change)).thenReturn(true);

        assertTrue(gymApp.changeTraineePassword(traineeLogin, change));
    }

    @Test
    @DisplayName("deleteTrainee: requires trainee authentication then hard-deletes")
    void deleteTrainee_requiresLogin() {
        when(traineeService.credentialsMatch(traineeLogin)).thenReturn(true);
        gymApp.deleteTrainee(traineeLogin, "trainee.user");
        verify(traineeService).delete("trainee.user");
    }

    @Test
    @DisplayName("findTraineeByUsername: requires trainee authentication before lookup")
    void findTraineeByUsername_requiresLogin() {
        when(traineeService.credentialsMatch(traineeLogin)).thenReturn(true);
        when(traineeService.findByUsername("trainee.user")).thenReturn(Optional.empty());
        assertTrue(gymApp.findTraineeByUsername(traineeLogin, "trainee.user").isEmpty());
    }

    @Test
    @DisplayName("findAllTrainees: requires trainee authentication before listing")
    void findAllTrainees_requiresLogin() {
        when(traineeService.credentialsMatch(traineeLogin)).thenReturn(true);
        when(traineeService.findAll()).thenReturn(List.of());
        assertTrue(gymApp.findAllTrainees(traineeLogin).isEmpty());
    }

    @Test
    @DisplayName("updateTraineeTrainers: requires trainee authentication before updating")
    void updateTraineeTrainers_requiresLogin() {
        TraineeUpdateTrainersRequest request = new TraineeUpdateTrainersRequest();
        when(traineeService.credentialsMatch(traineeLogin)).thenReturn(true);
        when(traineeService.updateTrainersList(request)).thenReturn(List.of());
        assertTrue(gymApp.updateTraineeTrainers(traineeLogin, request).isEmpty());
    }

    @Test
    @DisplayName("findNotAssignedTrainers: requires trainee authentication before listing")
    void findNotAssignedTrainers_requiresTraineeLogin() {
        when(traineeService.credentialsMatch(traineeLogin)).thenReturn(true);
        when(trainerService.findNotAssignedOnTrainee("trainee.user")).thenReturn(List.of());
        assertTrue(gymApp.findNotAssignedTrainers(traineeLogin, "trainee.user").isEmpty());
    }

    @Test
    @DisplayName("findAllTrainers: requires trainer authentication before listing")
    void findAllTrainers_requiresTrainerLogin() {
        when(trainerService.credentialsMatch(trainerLogin)).thenReturn(true);
        when(trainerService.findAll()).thenReturn(List.of());
        assertTrue(gymApp.findAllTrainers(trainerLogin).isEmpty());
    }

    @Test
    @DisplayName("findTrainingsByTrainer: requires trainer authentication before listing")
    void findTrainingsByTrainer_requiresTrainerLogin() {
        GetTrainingsByTrainerRequest request = new GetTrainingsByTrainerRequest();
        when(trainerService.credentialsMatch(trainerLogin)).thenReturn(true);
        when(trainingService.findByTrainer(request)).thenReturn(List.of());
        assertTrue(gymApp.findTrainingsByTrainer(trainerLogin, request).isEmpty());
    }

    @Test
    @DisplayName("changeTrainerPassword: requires trainer authentication before changing password")
    void changeTrainerPassword_requiresLogin() {
        ChangeLoginRequest change = new ChangeLoginRequest("trainer.user", "pass", "new");
        when(trainerService.credentialsMatch(trainerLogin)).thenReturn(true);
        when(trainerService.changePassword(change)).thenReturn(true);

        assertTrue(gymApp.changeTrainerPassword(trainerLogin, change));
    }

    @Test
    @DisplayName("findTrainerByUsername: requires trainer authentication before lookup")
    void findTrainerByUsername_requiresLogin() {
        when(trainerService.credentialsMatch(trainerLogin)).thenReturn(true);
        when(trainerService.findByUsername("trainer.user")).thenReturn(Optional.empty());
        assertTrue(gymApp.findTrainerByUsername(trainerLogin, "trainer.user").isEmpty());
    }
}

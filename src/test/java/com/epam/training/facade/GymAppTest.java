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
        TraineeCreateResponse response = new TraineeCreateResponse("trainee.user", "pass", null);
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
        when(traineeService.update("trainee.user", request)).thenReturn(response);

        assertEquals(response, gymApp.updateTrainee(traineeLogin, "trainee.user", request));

        verify(traineeService).credentialsMatch(traineeLogin);
        verify(traineeService).update("trainee.user", request);
    }

    @Test
    @DisplayName("updateTrainee: throws SecurityException when credentials are invalid")
    void updateTrainee_rejectsInvalidLogin() {
        TraineeUpdateRequest request = new TraineeUpdateRequest();
        when(traineeService.credentialsMatch(traineeLogin)).thenReturn(false);

        assertThrows(SecurityException.class, () -> gymApp.updateTrainee(traineeLogin, "trainee.user", request));

        verify(traineeService, never()).update(any(), any());
    }

    @Test
    @DisplayName("updateTrainee: throws SecurityException when updating another user's profile")
    void updateTrainee_rejectsCrossUserAccess() {
        TraineeUpdateRequest request = new TraineeUpdateRequest();
        when(traineeService.credentialsMatch(traineeLogin)).thenReturn(true);

        assertThrows(SecurityException.class, () -> gymApp.updateTrainee(traineeLogin, "other.user", request));

        verify(traineeService, never()).update(any(), any());
    }

    @Test
    @DisplayName("createTrainer: does not require authentication")
    void createTrainer_doesNotRequireLogin() {
        TrainerCreateRequest request = new TrainerCreateRequest();
        TrainerCreateResponse response = new TrainerCreateResponse("trainer.user", "pass", null);
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
        when(trainerService.update("trainer.user", request)).thenReturn(response);

        assertEquals(response, gymApp.updateTrainer(trainerLogin, "trainer.user", request));

        verify(trainerService).credentialsMatch(trainerLogin);
        verify(trainerService).update("trainer.user", request);
    }

    @Test
    @DisplayName("updateTrainer: throws SecurityException when credentials are invalid")
    void updateTrainer_rejectsInvalidLogin() {
        TrainerUpdateRequest request = new TrainerUpdateRequest();
        when(trainerService.credentialsMatch(trainerLogin)).thenReturn(false);

        assertThrows(SecurityException.class, () -> gymApp.updateTrainer(trainerLogin, "trainer.user", request));

        verify(trainerService, never()).update(any(), any());
    }

    @Test
    @DisplayName("updateTrainer: throws SecurityException when updating another user's profile")
    void updateTrainer_rejectsCrossUserAccess() {
        TrainerUpdateRequest request = new TrainerUpdateRequest();
        when(trainerService.credentialsMatch(trainerLogin)).thenReturn(true);

        assertThrows(SecurityException.class, () -> gymApp.updateTrainer(trainerLogin, "other.user", request));

        verify(trainerService, never()).update(any(), any());
    }

    @Test
    @DisplayName("createTraining: requires trainer authentication before delegating")
    void createTraining_requiresTrainerLoginAndDelegates() {
        TrainingCreateRequest request = new TrainingCreateRequest();
        request.setTrainer("trainer.user");
        when(trainerService.credentialsMatch(trainerLogin)).thenReturn(true);
        when(trainingService.create(request)).thenReturn(true);

        assertTrue(gymApp.createTraining(trainerLogin, request));

        verify(trainerService).credentialsMatch(trainerLogin);
        verify(trainingService).create(request);
    }

    @Test
    @DisplayName("createTraining: throws SecurityException when creating training for another trainer")
    void createTraining_rejectsCrossUserAccess() {
        TrainingCreateRequest request = new TrainingCreateRequest();
        request.setTrainer("other.trainer");
        when(trainerService.credentialsMatch(trainerLogin)).thenReturn(true);

        assertThrows(SecurityException.class, () -> gymApp.createTraining(trainerLogin, request));

        verify(trainingService, never()).create(any());
    }

    @Test
    @DisplayName("findTrainingsByTrainee: requires trainee authentication before delegating")
    void findTrainingsByTrainee_requiresTraineeLoginAndDelegates() {
        var pageable = org.springframework.data.domain.PageRequest.of(0, 10);
        var response = new org.springframework.data.domain.PageImpl<>(List.of(new GetTrainingsByTraineeResponse()));
        when(traineeService.credentialsMatch(traineeLogin)).thenReturn(true);
        when(trainingService.findByTrainee("trainee.user", null, null, null, null, pageable)).thenReturn(response);

        assertEquals(response, gymApp.findTrainingsByTrainee(traineeLogin, "trainee.user", null, null, null, null, pageable));

        verify(traineeService).credentialsMatch(traineeLogin);
        verify(trainingService).findByTrainee("trainee.user", null, null, null, null, pageable);
    }

    @Test
    @DisplayName("findTrainingsByTrainee: throws SecurityException when accessing another trainee's trainings")
    void findTrainingsByTrainee_rejectsCrossUserAccess() {
        var pageable = org.springframework.data.domain.PageRequest.of(0, 10);
        when(traineeService.credentialsMatch(traineeLogin)).thenReturn(true);

        assertThrows(SecurityException.class,
                () -> gymApp.findTrainingsByTrainee(traineeLogin, "other.user", null, null, null, null, pageable));

        verify(trainingService, never()).findByTrainee(any(), any(), any(), any(), any(), any());
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
    @DisplayName("deleteTrainee: throws SecurityException when deleting another user's profile")
    void deleteTrainee_rejectsCrossUserAccess() {
        when(traineeService.credentialsMatch(traineeLogin)).thenReturn(true);

        assertThrows(SecurityException.class, () -> gymApp.deleteTrainee(traineeLogin, "other.user"));

        verify(traineeService, never()).delete(any());
    }

    @Test
    @DisplayName("activateTrainee: throws SecurityException when activating another user's profile")
    void activateTrainee_rejectsCrossUserAccess() {
        when(traineeService.credentialsMatch(traineeLogin)).thenReturn(true);

        assertThrows(SecurityException.class, () -> gymApp.activateTrainee(traineeLogin, "other.user"));

        verify(traineeService, never()).activate(any());
    }

    @Test
    @DisplayName("deactivateTrainee: throws SecurityException when deactivating another user's profile")
    void deactivateTrainee_rejectsCrossUserAccess() {
        when(traineeService.credentialsMatch(traineeLogin)).thenReturn(true);

        assertThrows(SecurityException.class, () -> gymApp.deactivateTrainee(traineeLogin, "other.user"));

        verify(traineeService, never()).deactivate(any());
    }

    @Test
    @DisplayName("activateTrainer: throws SecurityException when activating another user's profile")
    void activateTrainer_rejectsCrossUserAccess() {
        when(trainerService.credentialsMatch(trainerLogin)).thenReturn(true);

        assertThrows(SecurityException.class, () -> gymApp.activateTrainer(trainerLogin, "other.user"));

        verify(trainerService, never()).activate(any());
    }

    @Test
    @DisplayName("deactivateTrainer: throws SecurityException when deactivating another user's profile")
    void deactivateTrainer_rejectsCrossUserAccess() {
        when(trainerService.credentialsMatch(trainerLogin)).thenReturn(true);

        assertThrows(SecurityException.class, () -> gymApp.deactivateTrainer(trainerLogin, "other.user"));

        verify(trainerService, never()).deactivate(any());
    }


    @Test
    @DisplayName("findTraineeByUsername: requires trainee authentication before lookup")
    void findTraineeByUsername_requiresLogin() {
        TraineeGetResponse response = new TraineeGetResponse();
        when(traineeService.credentialsMatch(traineeLogin)).thenReturn(true);
        when(traineeService.findByUsername("trainee.user")).thenReturn(response);
        assertEquals(response, gymApp.findTraineeByUsername(traineeLogin, "trainee.user"));
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
        when(traineeService.updateTrainersList("trainee.user", request)).thenReturn(List.of());
        assertTrue(gymApp.updateTraineeTrainers(traineeLogin, "trainee.user", request).isEmpty());
    }

    @Test
    @DisplayName("updateTraineeTrainers: throws SecurityException when updating another trainee's trainers")
    void updateTraineeTrainers_rejectsCrossUserAccess() {
        TraineeUpdateTrainersRequest request = new TraineeUpdateTrainersRequest();
        when(traineeService.credentialsMatch(traineeLogin)).thenReturn(true);

        assertThrows(SecurityException.class, () -> gymApp.updateTraineeTrainers(traineeLogin, "other.user", request));

        verify(traineeService, never()).updateTrainersList(any(), any());
    }

    @Test
    @DisplayName("findNotAssignedTrainers: requires trainee authentication before listing")
    void findNotAssignedTrainers_requiresTraineeLogin() {
        var pageable = org.springframework.data.domain.PageRequest.of(0, 10);
        when(traineeService.credentialsMatch(traineeLogin)).thenReturn(true);
        when(trainerService.findNotAssignedOnTrainee("trainee.user", pageable))
                .thenReturn(org.springframework.data.domain.Page.empty());
        assertTrue(gymApp.findNotAssignedTrainers(traineeLogin, "trainee.user", pageable).isEmpty());
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
        var pageable = org.springframework.data.domain.PageRequest.of(0, 10);
        when(trainerService.credentialsMatch(trainerLogin)).thenReturn(true);
        when(trainingService.findByTrainer("trainer.user", null, null, null, pageable))
                .thenReturn(org.springframework.data.domain.Page.empty());
        assertTrue(gymApp.findTrainingsByTrainer(trainerLogin, "trainer.user", null, null, null, pageable).isEmpty());
    }

    @Test
    @DisplayName("findTrainingsByTrainer: throws SecurityException when accessing another trainer's trainings")
    void findTrainingsByTrainer_rejectsCrossUserAccess() {
        var pageable = org.springframework.data.domain.PageRequest.of(0, 10);
        when(trainerService.credentialsMatch(trainerLogin)).thenReturn(true);

        assertThrows(SecurityException.class,
                () -> gymApp.findTrainingsByTrainer(trainerLogin, "other.user", null, null, null, pageable));

        verify(trainingService, never()).findByTrainer(any(), any(), any(), any(), any());
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
        TrainerGetResponse response = new TrainerGetResponse();
        when(trainerService.credentialsMatch(trainerLogin)).thenReturn(true);
        when(trainerService.findByUsername("trainer.user")).thenReturn(response);
        assertEquals(response, gymApp.findTrainerByUsername(trainerLogin, "trainer.user"));
    }
}

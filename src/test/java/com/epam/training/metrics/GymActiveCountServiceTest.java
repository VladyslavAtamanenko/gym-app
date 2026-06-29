package com.epam.training.metrics;

import com.epam.training.dao.TraineeDao;
import com.epam.training.dao.TrainerDao;
import com.epam.training.model.Trainee;
import com.epam.training.model.Trainer;
import com.epam.training.model.TrainingType;
import com.epam.training.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GymActiveCountService")
class GymActiveCountServiceTest {

    @Mock private TraineeDao traineeDao;
    @Mock private TrainerDao trainerDao;

    @InjectMocks
    private GymActiveCountService service;

    private User activeUser(long id) {
        return new User(id, "First", "Last", "First.Last" + id, "pass", true);
    }

    private User inactiveUser(long id) {
        return new User(id, "First", "Last", "First.Last" + id, "pass", false);
    }

    // --- countActiveTrainees ---

    @Test
    @DisplayName("countActiveTrainees: counts only trainees with isActive=true")
    void countActiveTrainees_onlyActive() {
        Trainee active = Trainee.builder().id(1L).user(activeUser(1L)).build();
        Trainee inactive = Trainee.builder().id(2L).user(inactiveUser(2L)).build();
        when(traineeDao.findAll()).thenReturn(List.of(active, inactive));

        assertEquals(1, service.countActiveTrainees());
    }

    @Test
    @DisplayName("countActiveTrainees: returns 0 when all trainees are inactive")
    void countActiveTrainees_allInactive() {
        Trainee t = Trainee.builder().id(1L).user(inactiveUser(1L)).build();
        when(traineeDao.findAll()).thenReturn(List.of(t));

        assertEquals(0, service.countActiveTrainees());
    }

    @Test
    @DisplayName("countActiveTrainees: returns 0 when no trainees exist")
    void countActiveTrainees_empty() {
        when(traineeDao.findAll()).thenReturn(List.of());

        assertEquals(0, service.countActiveTrainees());
    }

    // --- countActiveTrainers ---

    @Test
    @DisplayName("countActiveTrainers: counts only trainers with isActive=true")
    void countActiveTrainers_onlyActive() {
        TrainingType yoga = new TrainingType(1L, "Yoga");
        Trainer active = Trainer.builder().id(1L).user(activeUser(10L)).specialization(yoga).build();
        Trainer inactive = Trainer.builder().id(2L).user(inactiveUser(11L)).specialization(yoga).build();
        when(trainerDao.findAll()).thenReturn(List.of(active, inactive));

        assertEquals(1, service.countActiveTrainers());
    }

    @Test
    @DisplayName("countActiveTrainers: returns 0 when no trainers exist")
    void countActiveTrainers_empty() {
        when(trainerDao.findAll()).thenReturn(List.of());

        assertEquals(0, service.countActiveTrainers());
    }
}

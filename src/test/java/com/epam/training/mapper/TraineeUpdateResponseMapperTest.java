package com.epam.training.mapper;

import com.epam.training.dto.TraineeUpdateResponse;
import com.epam.training.dto.TrainerDTO;
import com.epam.training.model.Trainee;
import com.epam.training.model.Trainer;
import com.epam.training.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("TraineeUpdateResponseMapper")
class TraineeUpdateResponseMapperTest {

    @Mock private ToDTOMapper<Trainer, TrainerDTO> trainerMapper;

    private TraineeUpdateResponseMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new TraineeUpdateResponseMapper();
        mapper.setTrainerMapper(trainerMapper);
    }

    @Test
    @DisplayName("toDTO: maps username, name, isActive and trainer list")
    void toDTO_mapsUpdateResponseFields() {
        User user = new User(1L, "John", "Doe", "John.Doe", "pw", false);
        Trainer trainer = Trainer.builder().user(User.builder().username("Trainer.One").build()).build();
        Trainee trainee = Trainee.builder()
                .address("Street")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .user(user)
                .trainers(List.of(trainer))
                .build();
        when(trainerMapper.toDTO(trainer)).thenReturn(new TrainerDTO());

        TraineeUpdateResponse dto = mapper.toDTO(trainee);

        assertEquals("John.Doe", dto.getUsername());
        assertEquals("John", dto.getFirstName());
        assertEquals("Doe", dto.getLastName());
        assertFalse(dto.getIsActive());
        assertEquals(1, dto.getTrainers().size());
    }
}

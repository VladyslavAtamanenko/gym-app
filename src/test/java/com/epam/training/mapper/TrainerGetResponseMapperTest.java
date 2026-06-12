package com.epam.training.mapper;

import com.epam.training.dto.TraineeDTO;
import com.epam.training.dto.TrainerGetResponse;
import com.epam.training.model.Trainee;
import com.epam.training.model.Trainer;
import com.epam.training.model.TrainingType;
import com.epam.training.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("TrainerGetResponseMapper")
class TrainerGetResponseMapperTest {

    @Mock private ToDTOMapper<Trainee, TraineeDTO> traineeMapper;

    private TrainerGetResponseMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new TrainerGetResponseMapper();
        mapper.setTraineeMapper(traineeMapper);
    }

    @Test
    @DisplayName("toDTO: maps all trainer profile fields and trainee list")
    void toDTO_mapsTrainerFields() {
        User user = new User(1L, "Jane", "Smith", "Jane.Smith", "pw", true);
        Trainee trainee = Trainee.builder().user(User.builder().username("Trainee.One").build()).build();
        Trainer trainer = Trainer.builder()
                .user(user)
                .specialization(TrainingType.builder().name("Yoga").build())
                .trainees(List.of(trainee))
                .build();
        when(traineeMapper.toDTO(trainee)).thenReturn(new TraineeDTO());

        TrainerGetResponse dto = mapper.toDTO(trainer);

        assertEquals("Jane", dto.getFirstName());
        assertEquals("Smith", dto.getLastName());
        assertEquals("Yoga", dto.getSpecialization());
        assertTrue(dto.getIsActive());
        assertEquals(1, dto.getTrainees().size());
    }
}

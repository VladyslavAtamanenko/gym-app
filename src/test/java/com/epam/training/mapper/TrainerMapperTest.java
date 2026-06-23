package com.epam.training.mapper;

import com.epam.training.dto.TrainerDTO;
import com.epam.training.model.Trainer;
import com.epam.training.model.TrainingType;
import com.epam.training.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("TrainerMapper")
class TrainerMapperTest {

    private final TrainerMapper mapper = new TrainerMapper();

    @Test
    @DisplayName("toDTO: maps username, firstName, lastName and specialization from trainer")
    void toDTO_mapsCurrentTrainerDTOFields() {
        User user = new User(1L, "Lee", "Chan", "Lee.Chan", "pw", true);
        Trainer trainer = Trainer.builder()
                .id(3L)
                .user(user)
                .specialization(new TrainingType(1L, "Fitness"))
                .build();

        TrainerDTO dto = mapper.toDTO(trainer);

        assertEquals("Lee.Chan", dto.getUsername());
        assertEquals("Lee", dto.getFirstName());
        assertEquals("Chan", dto.getLastName());
        assertEquals("Fitness", dto.getSpecialization());
    }
}

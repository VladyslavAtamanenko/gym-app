package com.epam.training.mapper;

import com.epam.training.dto.TraineeDTO;
import com.epam.training.model.Trainee;
import com.epam.training.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("TraineeMapper")
class TraineeMapperTest {

    private final TraineeMapper mapper = new TraineeMapper();

    @Test
    @DisplayName("toDTO: maps username, firstName and lastName from trainee's user")
    void toDTO_mapsCurrentTraineeDTOFields() {
        User user = new User(1L, "Sue", "Gray", "Sue.Gray", "pw", true);
        Trainee trainee = Trainee.builder().id(5L).user(user).build();

        TraineeDTO dto = mapper.toDTO(trainee);

        assertEquals("Sue.Gray", dto.getUsername());
        assertEquals("Sue", dto.getFirstName());
        assertEquals("Gray", dto.getLastName());
    }
}

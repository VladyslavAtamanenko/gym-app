package com.epam.training.security;

import com.epam.training.dao.TraineeDao;
import com.epam.training.dao.TrainerDao;
import com.epam.training.model.Trainee;
import com.epam.training.model.Trainer;
import com.epam.training.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserDetailsServiceImpl")
class UserDetailsServiceImplTest {

    @Mock private TraineeDao traineeDao;
    @Mock private TrainerDao trainerDao;
    @InjectMocks private UserDetailsServiceImpl service;

    private User user(String username) {
        return User.builder().username(username).password("hashed").isActive(true).build();
    }

    @Test
    @DisplayName("loadUserByUsername: returns ROLE_TRAINEE principal for a trainee")
    void loadsTrainee_withCorrectRole() {
        Trainee trainee = Trainee.builder().user(user("John.Doe")).build();
        when(traineeDao.findByUsername("John.Doe")).thenReturn(Optional.of(trainee));

        UserDetails result = service.loadUserByUsername("John.Doe");

        assertThat(result.getUsername()).isEqualTo("John.Doe");
        assertThat(result.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_TRAINEE");
    }

    @Test
    @DisplayName("loadUserByUsername: falls back to trainer when not found as trainee")
    void loadsTrainer_whenNotFoundAsTrainee() {
        Trainer trainer = Trainer.builder().user(user("Alice.Smith")).build();
        when(traineeDao.findByUsername("Alice.Smith")).thenReturn(Optional.empty());
        when(trainerDao.findByUsername("Alice.Smith")).thenReturn(Optional.of(trainer));

        UserDetails result = service.loadUserByUsername("Alice.Smith");

        assertThat(result.getUsername()).isEqualTo("Alice.Smith");
        assertThat(result.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_TRAINER");
    }

    @Test
    @DisplayName("loadUserByUsername: throws UsernameNotFoundException when user is in neither dao")
    void throwsUsernameNotFoundException_whenNotFound() {
        when(traineeDao.findByUsername("Unknown")).thenReturn(Optional.empty());
        when(trainerDao.findByUsername("Unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.loadUserByUsername("Unknown"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("Unknown");
    }

    @Test
    @DisplayName("loadUserByUsername: password is taken from the User entity")
    void loadsPassword_fromUserEntity() {
        User u = User.builder().username("John.Doe").password("$2a$bcrypt").isActive(true).build();
        Trainee trainee = Trainee.builder().user(u).build();
        when(traineeDao.findByUsername("John.Doe")).thenReturn(Optional.of(trainee));

        UserDetails result = service.loadUserByUsername("John.Doe");

        assertThat(result.getPassword()).isEqualTo("$2a$bcrypt");
    }
}

package com.epam.training.service;


import com.epam.training.dao.TraineeDao;
import com.epam.training.dao.TrainerDao;
import com.epam.training.model.Trainee;
import com.epam.training.model.Trainer;
import com.epam.training.model.User;
import com.epam.training.service.impl.UserUtil;
import com.epam.training.util.PasswordGenerator;
import com.epam.training.util.UsernameGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserUtilTest {

    @Mock
    private TraineeDao traineeDao;
    @Mock private TrainerDao trainerDao;
    @Mock private UsernameGenerator usernameGenerator;
    @Mock private PasswordGenerator passwordGenerator;

    @InjectMocks
    private UserUtil userUtil;

    private User makeUser(Long id, String first, String last, String username) {
        User u = new User();
        u.setId(id);
        u.setFirstName(first);
        u.setLastName(last);
        u.setUserName(username);
        u.setPassword("oldpass123");
        u.setIsActive(true);
        return u;
    }

    private Trainee traineeWith(String username) {
        Trainee t = new Trainee();
        t.setUser(makeUser(1L, "A", "B", username));
        return t;
    }

    private Trainer trainerWith(String username) {
        Trainer t = new Trainer();
        t.setUser(makeUser(2L, "C", "D", username));
        return t;
    }


    @Test
    @DisplayName("initializeUser: sets generated username, password, and isActive=true")
    void initializeUser_setsAllFields() {
        when(traineeDao.findAll()).thenReturn(List.of(traineeWith("Alice.Smith")));
        when(trainerDao.findAll()).thenReturn(List.of(trainerWith("Bob.Jones")));
        when(usernameGenerator.generate(eq("John"), eq("Doe"), anySet()))
                .thenReturn("John.Doe");
        when(passwordGenerator.generate()).thenReturn("generatedP");

        User user = makeUser(null, "John", "Doe", null);
        userUtil.initializeUser(user);

        assertEquals("John.Doe", user.getUserName());
        assertEquals("generatedP", user.getPassword());
        assertTrue(user.getIsActive());
    }

    @Test
    @DisplayName("initializeUser: passes combined trainee+trainer usernames to generator")
    void initializeUser_collectsExistingUsernames() {
        when(traineeDao.findAll()).thenReturn(List.of(traineeWith("Trainee.One")));
        when(trainerDao.findAll()).thenReturn(List.of(trainerWith("Trainer.One")));
        when(usernameGenerator.generate(anyString(), anyString(), anySet()))
                .thenReturn("New.User");
        when(passwordGenerator.generate()).thenReturn("pass123456");

        User user = makeUser(null, "New", "User", null);
        userUtil.initializeUser(user);

        // capture the Set passed to the generator and verify both sources are included
        ArgumentCaptor<Set<String>> captor = ArgumentCaptor.forClass(Set.class);
        verify(usernameGenerator).generate(eq("New"), eq("User"), captor.capture());

        Set<String> passed = captor.getValue();
        assertTrue(passed.contains("Trainee.One"));
        assertTrue(passed.contains("Trainer.One"));
    }

    @Test
    @DisplayName("initializeUser: works when no existing users are present")
    void initializeUser_noExistingUsers() {
        when(traineeDao.findAll()).thenReturn(List.of());
        when(trainerDao.findAll()).thenReturn(List.of());
        when(usernameGenerator.generate(eq("Jane"), eq("Doe"), anySet()))
                .thenReturn("Jane.Doe");
        when(passwordGenerator.generate()).thenReturn("pass123456");

        User user = makeUser(null, "Jane", "Doe", null);
        userUtil.initializeUser(user);

        assertEquals("Jane.Doe", user.getUserName());
    }


    @Test
    @DisplayName("updateUser: name unchanged — keeps old username, updates isActive; firstName/lastName NOT set on result")
    void updateUser_sameNameKeepsUsername() {
        User old = makeUser(1L, "John", "Doe", "John.Doe");
        old.setPassword("secret1234");
        old.setIsActive(true);

        User incoming = makeUser(null, "John", "Doe", null);
        incoming.setIsActive(false);

        User result = userUtil.updateUser(old, incoming);

        assertEquals(1L,        result.getId());
        assertEquals("John.Doe", result.getUserName());  // unchanged
        assertEquals("secret1234",   result.getPassword());   // preserved
        assertFalse(result.getIsActive());                 // updated

        verifyNoInteractions(usernameGenerator, traineeDao, trainerDao);
    }

    @Test
    @DisplayName("updateUser: first name changed — regenerates username")
    void updateUser_firstNameChanged_regeneratesUsername() {
        User old = makeUser(1L, "John", "Doe", "John.Doe");
        old.setPassword("secret1234");

        User incoming = makeUser(null, "Jonathan", "Doe", null);
        incoming.setIsActive(true);

        when(traineeDao.findAll()).thenReturn(List.of());
        when(trainerDao.findAll()).thenReturn(List.of());
        when(usernameGenerator.generate(eq("Jonathan"), eq("Doe"), anySet()))
                .thenReturn("Jonathan.Doe");

        User result = userUtil.updateUser(old, incoming);

        assertEquals("Jonathan.Doe", result.getUserName());
        assertEquals("Jonathan", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("secret1234", result.getPassword());   // password preserved
    }

    @Test
    @DisplayName("updateUser: last name changed — regenerates username")
    void updateUser_lastNameChanged_regeneratesUsername() {
        User old = makeUser(1L, "John", "Doe", "John.Doe");
        old.setPassword("pass123456");

        User incoming = makeUser(null, "John", "Smith", null);
        incoming.setIsActive(true);

        when(traineeDao.findAll()).thenReturn(List.of());
        when(trainerDao.findAll()).thenReturn(List.of());
        when(usernameGenerator.generate(eq("John"), eq("Smith"), anySet()))
                .thenReturn("John.Smith");

        User result = userUtil.updateUser(old, incoming);

        assertEquals("John.Smith", result.getUserName());
    }

    @Test
    @DisplayName("updateUser: preserves old ID regardless of name change")
    void updateUser_preservesId() {
        User old = makeUser(42L, "John", "Doe", "John.Doe");
        old.setPassword("pass123456");

        User incoming = makeUser(null, "Jane", "Doe", null);
        incoming.setIsActive(true);

        when(traineeDao.findAll()).thenReturn(List.of());
        when(trainerDao.findAll()).thenReturn(List.of());
        when(usernameGenerator.generate(anyString(), anyString(), anySet()))
                .thenReturn("Jane.Doe");

        User result = userUtil.updateUser(old, incoming);

        assertEquals(42L, result.getId());
    }

    @Test
    @DisplayName("updateUser: password is always taken from old user, never from incoming")
    void updateUser_passwordAlwaysPreservedFromOld() {
        User old = makeUser(1L, "John", "Doe", "John.Doe");
        old.setPassword("originalPassword");

        User incoming = makeUser(null, "John", "Doe", null);
        incoming.setPassword("attemptedOverwrite");
        incoming.setIsActive(true);

        User result = userUtil.updateUser(old, incoming);

        assertEquals("originalPassword", result.getPassword());
    }

    // helper so anySet() compiles cleanly
    @SuppressWarnings("unchecked")
    private static <T> Set<T> anySet() {
        return any(Set.class);
    }
}

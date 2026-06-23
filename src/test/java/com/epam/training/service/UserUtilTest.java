package com.epam.training.service;

import com.epam.training.dao.UserDao;
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

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserUtilTest {

    @Mock private UserDao userDao;
    @Mock private UsernameGenerator usernameGenerator;
    @Mock private PasswordGenerator passwordGenerator;

    @InjectMocks
    private UserUtil userUtil;

    private User makeUser(Long id, String first, String last, String username) {
        User u = new User();
        u.setId(id);
        u.setFirstName(first);
        u.setLastName(last);
        u.setUsername(username);
        u.setPassword("oldpass123");
        u.setIsActive(true);
        return u;
    }

    @Test
    @DisplayName("initializeUser: sets generated username, password, and isActive=true")
    void initializeUser_setsAllFields() {
        when(userDao.findAllUsernames()).thenReturn(Set.of("Alice.Smith", "Bob.Jones"));
        when(usernameGenerator.generate(eq("John"), eq("Doe"), anySet())).thenReturn("John.Doe");
        when(passwordGenerator.generate()).thenReturn("generatedP");

        User user = makeUser(null, "John", "Doe", null);
        userUtil.initializeUser(user);

        assertEquals("John.Doe", user.getUsername());
        assertEquals("generatedP", user.getPassword());
        assertTrue(user.getIsActive());
    }

    @Test
    @DisplayName("initializeUser: passes all existing usernames from users table to generator")
    void initializeUser_passesExistingUsernamesToGenerator() {
        when(userDao.findAllUsernames()).thenReturn(Set.of("Trainee.One", "Trainer.One"));
        when(usernameGenerator.generate(anyString(), anyString(), anySet())).thenReturn("New.User");
        when(passwordGenerator.generate()).thenReturn("pass123456");

        User user = makeUser(null, "New", "User", null);
        userUtil.initializeUser(user);

        ArgumentCaptor<Set<String>> captor = ArgumentCaptor.forClass(Set.class);
        verify(usernameGenerator).generate(eq("New"), eq("User"), captor.capture());
        assertTrue(captor.getValue().containsAll(Set.of("Trainee.One", "Trainer.One")));
    }

    @Test
    @DisplayName("initializeUser: works when no existing users are present")
    void initializeUser_noExistingUsers() {
        when(userDao.findAllUsernames()).thenReturn(Set.of());
        when(usernameGenerator.generate(eq("Jane"), eq("Doe"), anySet())).thenReturn("Jane.Doe");
        when(passwordGenerator.generate()).thenReturn("pass123456");

        User user = makeUser(null, "Jane", "Doe", null);
        userUtil.initializeUser(user);

        assertEquals("Jane.Doe", user.getUsername());
    }

    @Test
    @DisplayName("updateUser: username is always preserved from old user, even when name changes")
    void updateUser_usernameAlwaysPreserved() {
        User old = makeUser(1L, "John", "Doe", "John.Doe");
        User incoming = makeUser(null, "Jonathan", "Doe", null);
        incoming.setIsActive(true);

        User result = userUtil.updateUser(old, incoming);

        assertEquals("John.Doe", result.getUsername());
        verifyNoInteractions(usernameGenerator, userDao);
    }

    @Test
    @DisplayName("updateUser: firstName and lastName are taken from incoming user")
    void updateUser_appliesNewName() {
        User old = makeUser(1L, "John", "Doe", "John.Doe");
        User incoming = makeUser(null, "Jonathan", "Smith", null);
        incoming.setIsActive(true);

        User result = userUtil.updateUser(old, incoming);

        assertEquals("Jonathan", result.getFirstName());
        assertEquals("Smith", result.getLastName());
    }

    @Test
    @DisplayName("updateUser: isActive is taken from incoming user")
    void updateUser_appliesIsActive() {
        User old = makeUser(1L, "John", "Doe", "John.Doe");
        old.setIsActive(true);
        User incoming = makeUser(null, "John", "Doe", null);
        incoming.setIsActive(false);

        User result = userUtil.updateUser(old, incoming);

        assertFalse(result.getIsActive());
    }

    @Test
    @DisplayName("updateUser: password is always taken from old user")
    void updateUser_passwordPreservedFromOld() {
        User old = makeUser(1L, "John", "Doe", "John.Doe");
        old.setPassword("originalPassword");
        User incoming = makeUser(null, "John", "Doe", null);
        incoming.setPassword("attemptedOverwrite");
        incoming.setIsActive(true);

        User result = userUtil.updateUser(old, incoming);

        assertEquals("originalPassword", result.getPassword());
    }

    @Test
    @DisplayName("updateUser: preserves old ID")
    void updateUser_preservesId() {
        User old = makeUser(42L, "John", "Doe", "John.Doe");
        User incoming = makeUser(null, "Jane", "Doe", null);
        incoming.setIsActive(true);

        User result = userUtil.updateUser(old, incoming);

        assertEquals(42L, result.getId());
    }

    @SuppressWarnings("unchecked")
    private static <T> Set<T> anySet() {
        return any(Set.class);
    }
}

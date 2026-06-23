package com.epam.training.dao;

import com.epam.training.config.DaoTestAppConfig;
import com.epam.training.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(DaoTestAppConfig.class)
@Transactional
@DisplayName("UserDao")
class UserDaoTest {

    @Autowired
    private UserDao userDao;

    @PersistenceContext
    private EntityManager entityManager;

    private User persistUser(String firstName, String lastName, String username) {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setPassword("password");
        user.setIsActive(true);
        entityManager.persist(user);
        entityManager.flush();
        return user;
    }

    @Test
    @DisplayName("findAllUsernames: returns usernames of all persisted users")
    void findAllUsernames_returnsAllUsernames() {
        persistUser("John", "Doe", "John.Doe");
        persistUser("Alice", "Smith", "Alice.Smith");

        Set<String> usernames = userDao.findAllUsernames();

        assertTrue(usernames.contains("John.Doe"));
        assertTrue(usernames.contains("Alice.Smith"));
    }

    @Test
    @DisplayName("findAllUsernames: returns empty set when no users exist")
    void findAllUsernames_emptyWhenNoUsers() {
        Set<String> usernames = userDao.findAllUsernames();

        assertNotNull(usernames);
        assertTrue(usernames.isEmpty());
    }

    @Test
    @DisplayName("findAllUsernames: result contains username of every persisted user")
    void findAllUsernames_countMatchesPersistedUsers() {
        persistUser("A", "B", "A.B");
        persistUser("C", "D", "C.D");
        persistUser("E", "F", "E.F");

        Set<String> usernames = userDao.findAllUsernames();

        assertEquals(3, usernames.size());
    }
}

package com.epam.training.service.impl;

import com.epam.training.dao.UserDao;
import com.epam.training.model.User;
import com.epam.training.util.PasswordGenerator;
import com.epam.training.util.UsernameGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class UserUtil {

    private static final Logger log = LoggerFactory.getLogger(UserUtil.class);

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private UsernameGenerator usernameGenerator;

    private PasswordGenerator passwordGenerator;

    /**
     * Initializes username, hashed password, and active flag on a new user.
     *
     * @return the generated plaintext password (for inclusion in the create response)
     */
    public String initializeUser(User user) {
        if (user == null) {
            log.warn("Rejected user initialization because user is null");
            throw new IllegalArgumentException("User is null");
        }

        Set<String> existingUsernames = userDao.findAllUsernames();
        log.debug("Collected existing usernames for uniqueness check. count={}", existingUsernames.size());

        String plainPassword = passwordGenerator.generate();
        user.setUsername(usernameGenerator.generate(user.getFirstName(), user.getLastName(), existingUsernames));
        user.setPassword(passwordEncoder.encode(plainPassword));
        user.setIsActive(true);
        log.info("User initialized for new profile. userId={}", user.getId());
        return plainPassword;
    }

    public User updateUser(User oldUser, User newUser) {
        if (oldUser == null || newUser == null) {
            log.warn("Rejected user update because old or new user is null");
            throw new IllegalArgumentException("User is null");
        }

        User updated = new User();
        updated.setId(oldUser.getId());
        updated.setFirstName(newUser.getFirstName());
        updated.setLastName(newUser.getLastName());
        updated.setUsername(oldUser.getUsername());
        updated.setPassword(oldUser.getPassword());
        updated.setIsActive(newUser.getIsActive());
        log.info("User profile updated. userId={}", updated.getId());
        return updated;
    }

    @Autowired
    public void setUsernameGenerator(UsernameGenerator usernameGenerator) {
        this.usernameGenerator = usernameGenerator;
    }

    @Autowired
    public void setPasswordGenerator(PasswordGenerator passwordGenerator) {
        this.passwordGenerator = passwordGenerator;
    }
}

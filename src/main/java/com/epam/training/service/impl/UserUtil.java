package com.epam.training.service.impl;


import com.epam.training.dao.TraineeDao;
import com.epam.training.dao.TrainerDao;
import com.epam.training.model.User;
import com.epam.training.util.PasswordGenerator;
import com.epam.training.util.UsernameGenerator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserUtil {

    private static final Log LOGGER = LogFactory.getLog(UserUtil.class);

    @Autowired
    private TrainerDao trainerDao;
    @Autowired
    private TraineeDao traineeDao;

    private UsernameGenerator usernameGenerator;

    private PasswordGenerator passwordGenerator;

    public void initializeUser(User user){
        if (user == null) {
            LOGGER.warn("Rejected user initialization because user is null");
            throw new IllegalArgumentException("User is null");
        }

        Set<String> existingUsernames = getUsernames();

        String username = usernameGenerator.generate(
                user.getFirstName(),
                user.getLastName(),
                existingUsernames
        );

        user.setUserName(username);
        user.setPassword(passwordGenerator.generate());
        user.setIsActive(true);
        LOGGER.info("User initialized for new profile. userId=" + user.getId());
    }

    public User updateUser(User oldUser, User newUser){
        if (oldUser == null || newUser == null) {
            LOGGER.warn("Rejected user update because old or new user is null");
            throw new IllegalArgumentException("User is null");
        }

        User updated = new User();
        updated.setId(oldUser.getId());
        boolean nameChanged = !Objects.equals(oldUser.getFirstName(), newUser.getFirstName()) ||
                !Objects.equals(oldUser.getLastName(), newUser.getLastName());

        if(nameChanged){
            updated.setFirstName(newUser.getFirstName());
            updated.setLastName(newUser.getLastName());
            Set<String> existingUsernames = getUsernames();

            String username = usernameGenerator.generate(
                    newUser.getFirstName(),
                    newUser.getLastName(),
                    existingUsernames
            );
            updated.setUserName(username);
        } else{
            updated.setFirstName(oldUser.getFirstName());
            updated.setLastName(oldUser.getLastName());
            updated.setUserName(oldUser.getUserName());
        }
        updated.setPassword(oldUser.getPassword());
        updated.setIsActive(newUser.getIsActive());
        LOGGER.info("User profile updated. userId=" + updated.getId()
                + ", usernameRegenerated=" + nameChanged);
        return updated;
    }

    private Set<String> getUsernames(){
       Set<String> existingUsernames = traineeDao.findAll()
                .stream()
                .filter(t -> t.getUser() != null)
                .map(t -> t.getUser().getUserName())
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        existingUsernames.addAll(trainerDao.findAll()
                .stream()
                .filter(t -> t.getUser() != null)
                .map(t -> t.getUser().getUserName())
                .filter(Objects::nonNull)
                .collect(Collectors.toSet()));

        LOGGER.debug("Collected existing usernames for uniqueness check. count=" + existingUsernames.size());
        return existingUsernames;
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

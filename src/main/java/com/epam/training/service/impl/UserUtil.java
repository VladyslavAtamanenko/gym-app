package com.epam.training.service.impl;


import com.epam.training.dao.TraineeDao;
import com.epam.training.dao.TrainerDao;
import com.epam.training.model.User;
import com.epam.training.util.PasswordGenerator;
import com.epam.training.util.UsernameGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserUtil {

    @Autowired
    private TrainerDao trainerDao;
    @Autowired
    private TraineeDao traineeDao;

    private UsernameGenerator usernameGenerator;

    private PasswordGenerator passwordGenerator;

    public void initializeUser(User user){

        Set<String> existingUsernames = getUsernames();

        String username = usernameGenerator.generate(
                user.getFirstName(),
                user.getLastName(),
                existingUsernames
        );

        user.setUserName(username);
        user.setPassword(passwordGenerator.generate());
        user.setIsActive(true);
    }

    public User updateUser(User oldUser, User newUser){
        User updated = new User();
        updated.setId(oldUser.getId());
        if(!oldUser.getFirstName().equals(newUser.getFirstName()) ||
                !oldUser.getLastName().equals(newUser.getLastName())){
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
        return updated;
    }

    private Set<String> getUsernames(){
       Set<String> existingUsernames = traineeDao.findAll()
                .stream()
                .map(t -> t.getUser().getUserName())
                .collect(Collectors.toSet());

        existingUsernames.addAll(trainerDao.findAll()
                .stream()
                .map(t -> t.getUser().getUserName())
                .collect(Collectors.toSet()));

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

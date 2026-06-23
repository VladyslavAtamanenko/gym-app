package com.epam.training.dao;

import java.util.Set;

public interface UserDao {
    Set<String> findAllUsernames();
}

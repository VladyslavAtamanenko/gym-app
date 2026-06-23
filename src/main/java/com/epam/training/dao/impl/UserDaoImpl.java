package com.epam.training.dao.impl;

import com.epam.training.dao.UserDao;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Repository
@Transactional
public class UserDaoImpl implements UserDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Set<String> findAllUsernames() {
        return new HashSet<>(entityManager
                .createQuery("SELECT u.username FROM User u", String.class)
                .getResultList());
    }
}

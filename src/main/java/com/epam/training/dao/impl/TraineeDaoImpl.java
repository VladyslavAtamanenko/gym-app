package com.epam.training.dao.impl;

import com.epam.training.dao.TraineeDao;
import com.epam.training.model.Trainee;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TraineeDaoImpl implements TraineeDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Trainee save(Trainee trainee) {

        if (trainee == null) {
            throw new IllegalArgumentException("Trainee is null");
        }

        if (trainee.getId() == null) {
            entityManager.persist(trainee);
            return trainee;
        }

        return entityManager.merge(trainee);
    }

    @Override
    public Optional<Trainee> findByUsername(String username) {

        TypedQuery<Trainee> query = entityManager.createQuery(
                "SELECT t FROM Trainee t WHERE t.username = :username",
                Trainee.class
        );

        query.setParameter("username", username);

        return query.getResultStream().findFirst();
    }

    @Override
    public List<Trainee> findAll() {
        return entityManager.createQuery(
                "SELECT t FROM Trainee t",
                Trainee.class
        ).getResultList();
    }

    @Override
    public void delete(String username) {
        findByUsername(username)
                .ifPresent(entityManager::remove);
    }
}
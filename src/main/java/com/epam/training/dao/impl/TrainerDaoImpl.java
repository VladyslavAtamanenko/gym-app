package com.epam.training.dao.impl;

import com.epam.training.dao.TrainerDao;
import com.epam.training.model.Trainee;
import com.epam.training.model.Trainer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TrainerDaoImpl implements TrainerDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Trainer save(Trainer trainer) {
        if (trainer == null) {
            throw new IllegalArgumentException("Trainer is null");
        }

        if (trainer.getId() == null) {
            entityManager.persist(trainer);
            return trainer;
        }

        return entityManager.merge(trainer);
    }

    @Override
    public List<Trainer> findAll() {
        return entityManager.createQuery(
                "SELECT t FROM Trainer t",
                Trainer.class
        ).getResultList();
    }

    @Override
    public Optional<Trainer> findByUsername(String username) {
        TypedQuery<Trainer> query = entityManager.createQuery(
                "SELECT t FROM Trainer t WHERE t.username = :username",
                Trainer.class
        );

        query.setParameter("username", username);

        return query.getResultStream().findFirst();
    }
}

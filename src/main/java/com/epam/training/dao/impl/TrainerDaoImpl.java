package com.epam.training.dao.impl;

import com.epam.training.dao.TrainerDao;
import com.epam.training.model.Trainee;
import com.epam.training.model.Trainer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class TrainerDaoImpl implements TrainerDao {

    private static final Log LOGGER = LogFactory.getLog(TrainerDaoImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Trainer save(Trainer trainer) {
        if (trainer == null) {
            LOGGER.warn("Rejected attempt to save null trainer");
            throw new IllegalArgumentException("Trainer is null");
        }

        if (trainer.getId() == null) {
            entityManager.persist(trainer);
            LOGGER.info("Created trainer. trainerId=" + trainer.getId()
                    + ", trainerUsername=" + getUsername(trainer));
            return trainer;
        }

        Trainer saved = entityManager.merge(trainer);
        LOGGER.info("Updated trainer. trainerId=" + saved.getId()
                + ", trainerUsername=" + getUsername(saved));
        return saved;
    }

    @Override
    public List<Trainer> findAll() {
        List<Trainer> trainers = entityManager.createQuery(
                "SELECT t FROM Trainer t",
                Trainer.class
        ).getResultList();
        LOGGER.debug("Retrieved trainers. count=" + trainers.size());
        return trainers;
    }

    @Override
    public Optional<Trainer> findByUsername(String username) {
        TypedQuery<Trainer> query = entityManager.createQuery(
                "SELECT t FROM Trainer t WHERE t.user.username = :username",
                Trainer.class
        );

        query.setParameter("username", username);

        Optional<Trainer> result = query.getResultStream().findFirst();
        LOGGER.debug("Trainer lookup completed. trainerUsername=" + username
                + ", found=" + result.isPresent());
        return result;
    }

    @Override
    public List<Trainer> findNotAssignedOnTrainee(String traineeUsername) {
        TypedQuery<Trainer> query = entityManager.createQuery(
                """
                SELECT tr
                FROM Trainer tr
                WHERE tr.user.isActive = true
                AND tr.id NOT IN (
                    SELECT assigned.id
                    FROM Trainee te
                    JOIN te.trainers assigned
                    WHERE te.user.username = :traineeUsername
                )
                """,
                Trainer.class
        );
        query.setParameter("traineeUsername", traineeUsername);

        List<Trainer> trainers = query.getResultList();
        LOGGER.debug("Retrieved trainers not assigned to trainee. traineeUsername=" + traineeUsername
                + ", count=" + trainers.size());
        return trainers;
    }

    private String getUsername(Trainer trainer) {
        return trainer.getUser() == null ? null : trainer.getUser().getUsername();
    }
}

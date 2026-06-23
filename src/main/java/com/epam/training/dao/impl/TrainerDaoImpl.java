package com.epam.training.dao.impl;

import com.epam.training.dao.TrainerDao;
import com.epam.training.model.Trainer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class TrainerDaoImpl implements TrainerDao {

    private static final Logger log = LoggerFactory.getLogger(TrainerDaoImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Trainer save(Trainer trainer) {
        if (trainer == null) {
            log.warn("Rejected attempt to save null trainer");
            throw new IllegalArgumentException("Trainer is null");
        }

        if (trainer.getId() == null) {
            entityManager.persist(trainer);
            log.info("Created trainer. trainerId={}, trainerUsername={}", trainer.getId(), getUsername(trainer));
            return trainer;
        }

        Trainer saved = entityManager.merge(trainer);
        log.info("Updated trainer. trainerId={}, trainerUsername={}", saved.getId(), getUsername(saved));
        return saved;
    }

    @Override
    public List<Trainer> findAll() {
        List<Trainer> trainers = entityManager.createQuery(
                "SELECT t FROM Trainer t",
                Trainer.class
        ).getResultList();
        log.debug("Retrieved trainers. count={}", trainers.size());
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
        log.debug("Trainer lookup completed. trainerUsername={}, found={}", username, result.isPresent());
        return result;
    }

    @Override
    public Page<Trainer> findNotAssignedOnTrainee(String traineeUsername, Pageable pageable) {
        String where = """
                FROM Trainer tr
                WHERE tr.user.isActive = true
                AND tr.id NOT IN (
                    SELECT assigned.id
                    FROM Trainee te
                    JOIN te.trainers assigned
                    WHERE te.user.username = :traineeUsername
                )
                """;

        TypedQuery<Trainer> query = entityManager.createQuery("SELECT tr " + where, Trainer.class);
        query.setParameter("traineeUsername", traineeUsername);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        List<Trainer> content = query.getResultList();

        Long total = entityManager.createQuery("SELECT COUNT(tr) " + where, Long.class)
                .setParameter("traineeUsername", traineeUsername)
                .getSingleResult();

        log.debug("Retrieved trainers not assigned to trainee. traineeUsername={}, count={}", traineeUsername, content.size());
        return new PageImpl<>(content, pageable, total);
    }

    private String getUsername(Trainer trainer) {
        return trainer.getUser() == null ? null : trainer.getUser().getUsername();
    }
}

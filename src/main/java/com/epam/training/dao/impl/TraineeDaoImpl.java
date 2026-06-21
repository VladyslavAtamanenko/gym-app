package com.epam.training.dao.impl;

import com.epam.training.dao.TraineeDao;
import com.epam.training.model.Trainee;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class TraineeDaoImpl implements TraineeDao {

    private static final Logger log = LoggerFactory.getLogger(TraineeDaoImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Trainee save(Trainee trainee) {

        if (trainee == null) {
            log.warn("Rejected attempt to save null trainee");
            throw new IllegalArgumentException("Trainee is null");
        }

        if (trainee.getId() == null) {
            entityManager.persist(trainee);
            log.info("Created trainee. traineeId={}, traineeUsername={}", trainee.getId(), getUsername(trainee));
            return trainee;
        }

        Trainee saved = entityManager.merge(trainee);
        log.info("Updated trainee. traineeId={}, traineeUsername={}", saved.getId(), getUsername(saved));
        return saved;
    }

    @Override
    public Optional<Trainee> findByUsername(String username) {

        TypedQuery<Trainee> query = entityManager.createQuery(
                "SELECT t FROM Trainee t WHERE t.user.username = :username",
                Trainee.class
        );

        query.setParameter("username", username);

        Optional<Trainee> result = query.getResultStream().findFirst();
        log.debug("Trainee lookup completed. traineeUsername={}, found={}", username, result.isPresent());
        return result;
    }

    @Override
    public List<Trainee> findAll() {
        List<Trainee> trainees = entityManager.createQuery(
                "SELECT t FROM Trainee t",
                Trainee.class
        ).getResultList();
        log.debug("Retrieved trainees. count={}", trainees.size());
        return trainees;
    }

    @Override
    public void delete(String username) {
        Optional<Trainee> trainee = findByUsername(username);
        if (trainee.isEmpty()) {
            log.warn("Delete requested for missing trainee. traineeUsername={}", username);
            return;
        }
        entityManager.remove(entityManager.contains(trainee.get()) ? trainee.get() : entityManager.merge(trainee.get()));
        log.info("Deleted trainee. traineeUsername={}", username);
    }

    private String getUsername(Trainee trainee) {
        return trainee.getUser() == null ? null : trainee.getUser().getUsername();
    }
}

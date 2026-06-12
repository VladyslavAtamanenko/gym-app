package com.epam.training.dao.impl;

import com.epam.training.dao.TraineeDao;
import com.epam.training.model.Trainee;
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
public class TraineeDaoImpl implements TraineeDao {

    private static final Log LOGGER = LogFactory.getLog(TraineeDaoImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Trainee save(Trainee trainee) {

        if (trainee == null) {
            LOGGER.warn("Rejected attempt to save null trainee");
            throw new IllegalArgumentException("Trainee is null");
        }

        if (trainee.getId() == null) {
            entityManager.persist(trainee);
            LOGGER.info("Created trainee. traineeId=" + trainee.getId()
                    + ", traineeUsername=" + getUsername(trainee));
            return trainee;
        }

        Trainee saved = entityManager.merge(trainee);
        LOGGER.info("Updated trainee. traineeId=" + saved.getId()
                + ", traineeUsername=" + getUsername(saved));
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
        LOGGER.debug("Trainee lookup completed. traineeUsername=" + username
                + ", found=" + result.isPresent());
        return result;
    }

    @Override
    public List<Trainee> findAll() {
        List<Trainee> trainees = entityManager.createQuery(
                "SELECT t FROM Trainee t",
                Trainee.class
        ).getResultList();
        LOGGER.debug("Retrieved trainees. count=" + trainees.size());
        return trainees;
    }

    @Override
    public void delete(String username) {
        Optional<Trainee> trainee = findByUsername(username);
        if (trainee.isEmpty()) {
            LOGGER.warn("Delete requested for missing trainee. traineeUsername=" + username);
            return;
        }
        entityManager.remove(entityManager.contains(trainee.get()) ? trainee.get() : entityManager.merge(trainee.get()));
        LOGGER.info("Deleted trainee. traineeUsername=" + username);
    }

    private String getUsername(Trainee trainee) {
        return trainee.getUser() == null ? null : trainee.getUser().getUsername();
    }
}

package com.epam.training.dao.impl;

import com.epam.training.dao.TrainingDao;
import com.epam.training.model.Training;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Repository
@Transactional
public class TrainingDaoImpl implements TrainingDao {

    private static final Log LOGGER = LogFactory.getLog(TrainingDaoImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Training save(Training training) {
        if (training == null) {
            LOGGER.warn("Rejected attempt to save null training");
            throw new IllegalArgumentException("Training is null");
        }

        if (training.getId() == null) {
            entityManager.persist(training);
            LOGGER.info("Created training. trainingId=" + training.getId()
                    + ", traineeUsername=" + getTraineeUsername(training)
                    + ", trainerUsername=" + getTrainerUsername(training));
            return training;
        }

        Training saved = entityManager.merge(training);
        LOGGER.info("Updated training. trainingId=" + saved.getId()
                + ", traineeUsername=" + getTraineeUsername(saved)
                + ", trainerUsername=" + getTrainerUsername(saved));
        return saved;
    }

    @Override
    public List<Training> findByTrainer(String trainerUsername, LocalDate from, LocalDate to, String traineeUsername) {
        TypedQuery<Training> query = entityManager.createQuery(
                """
                SELECT tr
                FROM Training tr
                WHERE tr.trainer.user.username = :trainerUsername
                  AND (:fromDate IS NULL OR tr.date >= :fromDate)
                  AND (:toDate IS NULL OR tr.date <= :toDate)
                  AND (:traineeUsername IS NULL OR tr.trainee.user.username = :traineeUsername)
                """,
                Training.class
        );
        query.setParameter("trainerUsername", trainerUsername);
        query.setParameter("fromDate", from);
        query.setParameter("toDate", to);
        query.setParameter("traineeUsername", traineeUsername);

        List<Training> trainings = query.getResultList();
        LOGGER.debug("Retrieved trainings by trainer. trainerUsername=" + trainerUsername
                + ", traineeUsername=" + traineeUsername + ", count=" + trainings.size());
        return trainings;
    }

    @Override
    public List<Training> findByTrainee(String traineeUsername, String trainingType, LocalDate from,
                                        LocalDate to, String trainerUsername) {
        TypedQuery<Training> query = entityManager.createQuery(
                """
                SELECT tr
                FROM Training tr
                WHERE tr.trainee.user.username = :traineeUsername
                  AND (:trainingType IS NULL OR tr.type.name = :trainingType)
                  AND (:fromDate IS NULL OR tr.date >= :fromDate)
                  AND (:toDate IS NULL OR tr.date <= :toDate)
                  AND (:trainerUsername IS NULL OR tr.trainer.user.username = :trainerUsername)
                """,
                Training.class
        );
        query.setParameter("traineeUsername", traineeUsername);
        query.setParameter("trainingType", trainingType);
        query.setParameter("fromDate", from);
        query.setParameter("toDate", to);
        query.setParameter("trainerUsername", trainerUsername);

        List<Training> trainings = query.getResultList();
        LOGGER.debug("Retrieved trainings by trainee. traineeUsername=" + traineeUsername
                + ", trainerUsername=" + trainerUsername + ", trainingType=" + trainingType
                + ", count=" + trainings.size());
        return trainings;
    }

    private String getTraineeUsername(Training training) {
        if (training.getTrainee() == null || training.getTrainee().getUser() == null) {
            return null;
        }
        return training.getTrainee().getUser().getUsername();
    }

    private String getTrainerUsername(Training training) {
        if (training.getTrainer() == null || training.getTrainer().getUser() == null) {
            return null;
        }
        return training.getTrainer().getUser().getUsername();
    }
}

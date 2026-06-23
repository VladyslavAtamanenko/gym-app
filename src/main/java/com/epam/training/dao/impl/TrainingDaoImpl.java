package com.epam.training.dao.impl;

import com.epam.training.dao.TrainingDao;
import com.epam.training.model.Training;
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

import java.time.LocalDate;
import java.util.List;

@Repository
@Transactional
public class TrainingDaoImpl implements TrainingDao {

    private static final Logger log = LoggerFactory.getLogger(TrainingDaoImpl.class);

    private static final String BY_TRAINER_WHERE =
            """
            FROM Training tr
            WHERE tr.trainer.user.username = :trainerUsername
              AND (:fromDate IS NULL OR tr.date >= :fromDate)
              AND (:toDate IS NULL OR tr.date <= :toDate)
              AND (:traineeUsername IS NULL OR tr.trainee.user.username = :traineeUsername)
            """;

    private static final String BY_TRAINEE_WHERE =
            """
            FROM Training tr
            WHERE tr.trainee.user.username = :traineeUsername
              AND (:trainingType IS NULL OR tr.type.name = :trainingType)
              AND (:fromDate IS NULL OR tr.date >= :fromDate)
              AND (:toDate IS NULL OR tr.date <= :toDate)
              AND (:trainerUsername IS NULL OR tr.trainer.user.username = :trainerUsername)
            """;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Training save(Training training) {
        if (training == null) {
            log.warn("Rejected attempt to save null training");
            throw new IllegalArgumentException("Training is null");
        }

        if (training.getId() == null) {
            entityManager.persist(training);
            log.info("Created training. trainingId={}, traineeUsername={}, trainerUsername={}", training.getId(), getTraineeUsername(training), getTrainerUsername(training));
            return training;
        }

        Training saved = entityManager.merge(training);
        log.info("Updated training. trainingId={}, traineeUsername={}, trainerUsername={}", saved.getId(), getTraineeUsername(saved), getTrainerUsername(saved));
        return saved;
    }

    @Override
    public Page<Training> findByTrainer(String trainerUsername, LocalDate from, LocalDate to,
                                        String traineeUsername, Pageable pageable) {
        TypedQuery<Training> query = entityManager.createQuery("SELECT tr " + BY_TRAINER_WHERE, Training.class);
        setByTrainerParams(query, trainerUsername, from, to, traineeUsername);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        List<Training> content = query.getResultList();

        Long total = entityManager.createQuery("SELECT COUNT(tr) " + BY_TRAINER_WHERE, Long.class)
                .setParameter("trainerUsername", trainerUsername)
                .setParameter("fromDate", from)
                .setParameter("toDate", to)
                .setParameter("traineeUsername", traineeUsername)
                .getSingleResult();

        log.debug("Retrieved trainings by trainer. trainerUsername={}, count={}", trainerUsername, content.size());
        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<Training> findByTrainee(String traineeUsername, String trainingType, LocalDate from,
                                        LocalDate to, String trainerUsername, Pageable pageable) {
        TypedQuery<Training> query = entityManager.createQuery("SELECT tr " + BY_TRAINEE_WHERE, Training.class);
        setByTraineeParams(query, traineeUsername, trainingType, from, to, trainerUsername);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        List<Training> content = query.getResultList();

        Long total = entityManager.createQuery("SELECT COUNT(tr) " + BY_TRAINEE_WHERE, Long.class)
                .setParameter("traineeUsername", traineeUsername)
                .setParameter("trainingType", trainingType)
                .setParameter("fromDate", from)
                .setParameter("toDate", to)
                .setParameter("trainerUsername", trainerUsername)
                .getSingleResult();

        log.debug("Retrieved trainings by trainee. traineeUsername={}, count={}", traineeUsername, content.size());
        return new PageImpl<>(content, pageable, total);
    }

    private void setByTrainerParams(TypedQuery<?> query, String trainerUsername,
                                    LocalDate from, LocalDate to, String traineeUsername) {
        query.setParameter("trainerUsername", trainerUsername);
        query.setParameter("fromDate", from);
        query.setParameter("toDate", to);
        query.setParameter("traineeUsername", traineeUsername);
    }

    private void setByTraineeParams(TypedQuery<?> query, String traineeUsername, String trainingType,
                                    LocalDate from, LocalDate to, String trainerUsername) {
        query.setParameter("traineeUsername", traineeUsername);
        query.setParameter("trainingType", trainingType);
        query.setParameter("fromDate", from);
        query.setParameter("toDate", to);
        query.setParameter("trainerUsername", trainerUsername);
    }

    private String getTraineeUsername(Training training) {
        if (training.getTrainee() == null || training.getTrainee().getUser() == null) return null;
        return training.getTrainee().getUser().getUsername();
    }

    private String getTrainerUsername(Training training) {
        if (training.getTrainer() == null || training.getTrainer().getUser() == null) return null;
        return training.getTrainer().getUser().getUsername();
    }
}

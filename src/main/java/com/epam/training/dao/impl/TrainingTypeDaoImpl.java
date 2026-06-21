package com.epam.training.dao.impl;

import com.epam.training.dao.TrainingTypeDao;
import com.epam.training.model.TrainingType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.epam.training.exception.TrainingTypeNotFoundException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class TrainingTypeDaoImpl implements TrainingTypeDao {

    private static final Logger log = LoggerFactory.getLogger(TrainingTypeDaoImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public TrainingType findById(Long id) {
        TrainingType trainingType = entityManager.find(TrainingType.class, id);
        if (trainingType == null) {
            log.warn("Training type not found by id. trainingTypeId={}", id);
            throw new TrainingTypeNotFoundException(id);
        }
        log.debug("Found training type by id. trainingTypeId={}", id);
        return trainingType;
    }

    @Override
    public TrainingType findByName(String name) {
        TypedQuery<TrainingType> query = entityManager.createQuery(
                "SELECT tt FROM TrainingType tt WHERE tt.name = :name",
                TrainingType.class
        );
        query.setParameter("name", name);
        TrainingType trainingType = query.getResultStream()
                .findFirst()
                .orElseThrow(() -> {
                    log.warn("Training type not found by name. trainingTypeName={}", name);
                    return new TrainingTypeNotFoundException(name);
                });
        log.debug("Found training type by name. trainingTypeName={}", name);
        return trainingType;
    }

    @Override
    public List<TrainingType> findAll() {
        List<TrainingType> trainingTypes = entityManager.createQuery(
                "SELECT tt FROM TrainingType tt",
                TrainingType.class
        ).getResultList();
        log.debug("Retrieved training types. count={}", trainingTypes.size());
        return trainingTypes;
    }
}

package com.epam.training.dao.impl;

import com.epam.training.dao.TrainingTypeDao;
import com.epam.training.model.TrainingType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Repository
@Transactional
public class TrainingTypeDaoImpl implements TrainingTypeDao {

    private static final Log LOGGER = LogFactory.getLog(TrainingTypeDaoImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public TrainingType findById(Long id) {
        TrainingType trainingType = entityManager.find(TrainingType.class, id);
        if (trainingType == null) {
            LOGGER.warn("Training type not found by id. trainingTypeId=" + id);
            throw new NoSuchElementException();
        }
        LOGGER.debug("Found training type by id. trainingTypeId=" + id);
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
                    LOGGER.warn("Training type not found by name. trainingTypeName=" + name);
                    return new NoSuchElementException();
                });
        LOGGER.debug("Found training type by name. trainingTypeName=" + name);
        return trainingType;
    }

    @Override
    public List<TrainingType> findAll() {
        List<TrainingType> trainingTypes = entityManager.createQuery(
                "SELECT tt FROM TrainingType tt",
                TrainingType.class
        ).getResultList();
        LOGGER.debug("Retrieved training types. count=" + trainingTypes.size());
        return trainingTypes;
    }
}

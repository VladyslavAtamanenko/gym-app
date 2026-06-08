package com.epam.training.dao.impl;

import com.epam.training.dao.TraineeDao;
import com.epam.training.dao.TrainingDao;
import com.epam.training.model.Trainee;
import com.epam.training.model.Training;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class MapTraineeDao implements TraineeDao {

    private static final Log LOGGER = LogFactory.getLog(MapTraineeDao.class);

    @Autowired
    Map<Long, Trainee> storage;

    TrainingDao trainingDao;

    @Override
    public Trainee save(Trainee trainee) {

        if (trainee == null) {
            LOGGER.warn("Rejected attempt to save null trainee");
            throw new IllegalArgumentException("Trainee is null");
        }

        Long traineeId;
        boolean created = trainee.getId() == null;

        if (created) {
            traineeId = getNextId();
            trainee.setId(traineeId);
        }
        else {
            traineeId = trainee.getId();
        }

        storage.put(traineeId, trainee);
        LOGGER.info((created ? "Created" : "Updated") + " trainee. traineeId=" + traineeId
                + ", storageSize=" + storage.size());

        return storage.get(traineeId);
    }

    @Override
    public Optional<Trainee> findById(Long id) {
        Optional<Trainee> result = Optional.ofNullable(storage.get(id));
        if (result.isPresent()) {
            LOGGER.debug("Found trainee by id. traineeId=" + id);
        } else {
            LOGGER.debug("Trainee not found by id. traineeId=" + id);
        }
        return result;
    }

    @Override
    public Optional<Trainee> findByUsername(String username) {
        return Optional.empty();
    }

    @Override
    public List<Trainee> findAll() {
        LOGGER.debug("Retrieving all trainees. count=" + storage.size());
        return new ArrayList<>(storage.values());
    }

    @Override
    public void delete(String username) {
        Trainee removed = storage.remove(id);

        if (removed == null) {
            LOGGER.warn("Delete requested for missing trainee. traineeId=" + id);
            return;
        }

        List<Training> trainings = trainingDao.findByTrainee(id);

        if (!trainings.isEmpty()) {
            LOGGER.info("Cascade deleting trainings for trainee. traineeId=" + id +
                    ", trainingsCount=" + trainings.size());

            for (Training training : trainings) {
                trainingDao.delete(training.getId());
            }
        } else {
            LOGGER.debug("No trainings found for cascade delete. traineeId=" + id);
        }

        LOGGER.info("Deleted trainee. traineeId=" + id +
                ", storageSize=" + storage.size());
    }

    private Long getNextId() {
        return storage.keySet().stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0L) + 1;
    }

    @Autowired
    public void setTrainingDao(TrainingDao trainingDao) {
        this.trainingDao = trainingDao;
    }
}

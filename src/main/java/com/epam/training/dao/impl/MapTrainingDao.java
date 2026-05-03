package com.epam.training.dao.impl;

import com.epam.training.dao.TrainingDao;
import com.epam.training.model.Training;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class MapTrainingDao implements TrainingDao {

    private static final Log LOGGER = LogFactory.getLog(MapTrainingDao.class);

    @Autowired
    Map<Long, Training> storage;

    @Override
    public Training save(Training training) {

        if (training == null) {
            LOGGER.warn("Rejected attempt to save null training");
            throw new IllegalArgumentException("Training is null");
        }

        Long trainingId;
        boolean created = training.getId() == null;

        if (created) {
            trainingId = getNextId();
            training.setId(trainingId);
        }
        else {
            trainingId = training.getId();
        }

        storage.put(trainingId, training);
        LOGGER.info((created ? "Created" : "Updated") + " training. trainingId=" + trainingId
                + ", storageSize=" + storage.size());

        return storage.get(trainingId);
    }

    @Override
    public Optional<Training> findById(Long id) {
        Optional<Training> result = Optional.ofNullable(storage.get(id));
        if (result.isPresent()) {
            LOGGER.debug("Found training by id. trainingId=" + id);
        } else {
            LOGGER.debug("Training not found by id. trainingId=" + id);
        }
        return result;
    }

    @Override
    public List<Training> findAll() {
        LOGGER.debug("Retrieving all trainings. count=" + storage.size());
        return new ArrayList<>(storage.values());
    }

    private Long getNextId() {
        return storage.keySet().stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0L) + 1;
    }

}

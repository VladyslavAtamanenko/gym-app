package com.epam.training.dao.impl;

import com.epam.training.dao.TrainerDao;
import com.epam.training.model.Trainer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class MapTrainerDao implements TrainerDao {

    private static final Log LOGGER = LogFactory.getLog(MapTrainerDao.class);

    @Autowired
    Map<Long, Trainer> storage;

    @Override
    public Trainer save(Trainer trainer) {
        if (trainer == null) {
            LOGGER.warn("Rejected attempt to save null trainer");
            throw new IllegalArgumentException("Trainer is null");
        }

        Long trainerId;
        boolean created = trainer.getId() == null;

        if (created) {
            trainerId = getNextId();
            trainer.setId(trainerId);
        }
        else {
            trainerId = trainer.getId();
        }

        storage.put(trainerId, trainer);
        LOGGER.info((created ? "Created" : "Updated") + " trainer. trainerId=" + trainerId
                + ", storageSize=" + storage.size());

        return storage.get(trainerId);
    }

    @Override
    public Optional<Trainer> findById(Long id) {
        Optional<Trainer> result = Optional.ofNullable(storage.get(id));
        if (result.isPresent()) {
            LOGGER.debug("Found trainer by id. trainerId=" + id);
        } else {
            LOGGER.debug("Trainer not found by id. trainerId=" + id);
        }
        return result;
    }

    @Override
    public List<Trainer> findAll() {
        LOGGER.debug("Retrieving all trainers. count=" + storage.size());
        return new ArrayList<>(storage.values());
    }

    private Long getNextId() {
        return storage.keySet().stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0L) + 1;
    }
}

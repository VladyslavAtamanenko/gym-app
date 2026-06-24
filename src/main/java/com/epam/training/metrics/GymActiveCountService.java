package com.epam.training.metrics;

import com.epam.training.dao.TraineeDao;
import com.epam.training.dao.TrainerDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class GymActiveCountService {

    private final TraineeDao traineeDao;
    private final TrainerDao trainerDao;

    @Autowired
    public GymActiveCountService(TraineeDao traineeDao, TrainerDao trainerDao) {
        this.traineeDao = traineeDao;
        this.trainerDao = trainerDao;
    }

    public long countActiveTrainees() {
        return traineeDao.findAll().stream()
                .filter(t -> Boolean.TRUE.equals(t.getUser().getIsActive()))
                .count();
    }

    public long countActiveTrainers() {
        return trainerDao.findAll().stream()
                .filter(t -> Boolean.TRUE.equals(t.getUser().getIsActive()))
                .count();
    }
}

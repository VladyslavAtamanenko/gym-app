package com.epam.training.config;

import com.epam.training.model.Trainee;
import com.epam.training.model.Trainer;
import com.epam.training.model.Training;
import com.epam.training.model.TrainingType;
import com.epam.training.model.User;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Lazy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Component
public class StorageInitializer implements BeanPostProcessor {

    private static final Log LOGGER = LogFactory.getLog(StorageInitializer.class);

    @Value("${storage.trainee.file}")
    private Resource traineeFile;

    @Value("${storage.trainer.file}")
    private Resource trainerFile;

    @Value("${storage.training.file}")
    private Resource trainingFile;

    @Autowired
    @Lazy
    private Map<Long, Trainee> traineeMap;

    @Autowired
    @Lazy
    private Map<Long, Trainer> trainerMap;

    @Autowired
    @Lazy
    private Map<Long, Training> trainingMap;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Post-processing bean: " + beanName);
        }

        if (beanName.equals("traineeMap")) {
            traineeMap = castStorage(bean);
        }

        if (beanName.equals("trainerMap")) {
            trainerMap = castStorage(bean);
        }

        if (beanName.equals("trainingMap")) {
            trainingMap = castStorage(bean);
        }

        if (beanName.equals("trainingMap")) {
            LOGGER.info("Starting storage initialization from configured CSV resources");
            loadAll();
            LOGGER.info("Storage initialization completed. traineeCount=" + traineeMap.size()
                    + ", trainerCount=" + trainerMap.size()
                    + ", trainingCount=" + trainingMap.size());
        }

        return bean;
    }

    private void loadAll() {
        LOGGER.debug("Loading storage data in dependency order: trainees, trainers, trainings");
        loadTrainees();
        loadTrainers();
        loadTrainings();
    }

    private void loadTrainees() {
        readLines(traineeFile).forEach(line -> {
            String[] parts = line.split(",");

            Trainee trainee = new Trainee();
            trainee.setId(Long.parseLong(parts[0]));
            trainee.setDateOfBirth(LocalDate.parse(parts[1]));
            trainee.setAddress(parts[2]);
            trainee.setUser(userWithId(Long.parseLong(parts[3])));

            traineeMap.put(trainee.getId(), trainee);
        });
        LOGGER.info("Loaded trainees from CSV. count=" + traineeMap.size());
    }

    private void loadTrainers() {
        readLines(trainerFile).forEach(line -> {
            String[] parts = line.split(",");

            Trainer trainer = new Trainer();
            trainer.setId(Long.parseLong(parts[0]));

            TrainingType type = new TrainingType();
            type.setId(Long.parseLong(parts[1]));
            trainer.setSpecialization(type);
            trainer.setUser(userWithId(Long.parseLong(parts[2])));

            trainerMap.put(trainer.getId(), trainer);
        });
        LOGGER.info("Loaded trainers from CSV. count=" + trainerMap.size());
    }

    private void loadTrainings() {
        readLines(trainingFile).forEach(line -> {
            String[] parts = line.split(",");

            Training training = new Training();
            training.setId(Long.parseLong(parts[0]));
            Long traineeId = Long.parseLong(parts[1]);
            Long trainerId = Long.parseLong(parts[2]);
            training.setTrainee(traineeMap.get(traineeId));
            training.setTrainer(trainerMap.get(trainerId));
            training.setName(parts[3]);

            TrainingType type = new TrainingType();
            type.setId(Long.parseLong(parts[4]));
            training.setType(type);

            training.setDate(LocalDateTime.parse(parts[5]));
            training.setDuration(Duration.ofMinutes(Long.parseLong(parts[6])));

            if (training.getTrainee() == null || training.getTrainer() == null) {
                LOGGER.warn("Training CSV row references missing related entities. trainingId="
                        + training.getId() + ", traineeId=" + traineeId + ", trainerId=" + trainerId);
            }

            trainingMap.put(training.getId(), training);
        });
        LOGGER.info("Loaded trainings from CSV. count=" + trainingMap.size());
    }

    private List<String> readLines(Resource resource) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            List<String> lines = reader.lines()
                    .skip(1)
                    .filter(line -> !line.isBlank())
                    .toList();
            LOGGER.debug("Read CSV resource. resource=" + resource.getDescription()
                    + ", records=" + lines.size());
            return lines;
        } catch (IOException e) {
            LOGGER.error("Failed to read CSV resource. resource=" + resource.getDescription(), e);
            throw new RuntimeException(e);
        }
    }

    private User userWithId(Long id) {
        User user = new User();
        user.setId(id);
        return user;
    }

    @SuppressWarnings("unchecked")
    private <T> Map<Long, T> castStorage(Object bean) {
        return (Map<Long, T>) bean;
    }
}

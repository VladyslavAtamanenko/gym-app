package com.epam.training.service.impl;

import com.epam.training.dao.TrainerDao;
import com.epam.training.dto.*;
import com.epam.training.mapper.Mapper;
import com.epam.training.mapper.ToDTOMapper;
import com.epam.training.mapper.ToEntityMapper;
import com.epam.training.model.Trainee;
import com.epam.training.model.Trainer;
import com.epam.training.model.User;
import com.epam.training.service.TrainerService;
import com.epam.training.util.PasswordGenerator;
import com.epam.training.util.UsernameGenerator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TrainerServiceImpl implements TrainerService {

    private static final Log LOGGER = LogFactory.getLog(TrainerServiceImpl.class);

    private ToEntityMapper<TrainerCreateRequest, Trainer> trainerCreateRequestMapper;
    private ToDTOMapper<Trainer, TrainerCreateResponse> trainerCreateResponseMapper;
    private Mapper<Trainer, TrainerDTO> trainerMapper;
    private UserUtil userUtil;

    @Autowired
    private TrainerDao trainerDao;

    @Autowired
    private UsernameGenerator usernameGenerator;

    @Autowired
    private PasswordGenerator passwordGenerator;

    @Override
    public TrainerCreateResponse create(TrainerCreateRequest trainer) {
        if (trainer == null) {
            LOGGER.warn("Rejected trainer creation request because request body is null");
            throw new IllegalArgumentException();
        }
        LOGGER.debug("Creating trainer from request");
        Trainer created = trainerCreateRequestMapper.toEntity(trainer);
        User user = created.getUser();
        userUtil.initializeUser(user);
        Trainer saved = trainerDao.save(created);
        LOGGER.info("Trainer created successfully. trainerId=" + saved.getId());
        return trainerCreateResponseMapper.toDTO(saved);
    }

    @Override
    public TrainerDTO update(TrainerDTO trainer) {
        if (trainer == null) {
            LOGGER.warn("Rejected trainer update request because request body is null");
            throw new IllegalArgumentException();
        }
        LOGGER.debug("Updating trainer. trainerId=" + trainer.getId());
        Trainer updates = trainerMapper.toEntity(trainer);
        Trainer updated = trainerDao.findById(trainer.getId())
                .orElseThrow(() -> {
                    LOGGER.warn("Trainer update failed because trainer was not found. trainerId=" + trainer.getId());
                    return new NoSuchElementException();
                });
        updated.setSpecialization(trainer.getSpecialization());
        updated.setUser(userUtil.updateUser(updated.getUser(), updates.getUser()));
        Trainer saved = trainerDao.save(updated);
        LOGGER.info("Trainer updated successfully. trainerId=" + saved.getId());
        return trainerMapper.toDTO(saved);
    }

    @Override
    public Optional<TrainerDTO> findById(Long id) {
        Optional<TrainerDTO> result = trainerDao.findById(id).map(trainerMapper::toDTO);
        LOGGER.debug("Trainer lookup completed. trainerId=" + id + ", found=" + result.isPresent());
        return result;
    }

    @Override
    public List<TrainerDTO> findAll() {
        List<TrainerDTO> result = trainerDao.findAll()
                .stream()
                .map(trainerMapper::toDTO)
                .collect(Collectors.toList());
        LOGGER.debug("Trainer list retrieved. count=" + result.size());
        return result;
    }

    @Autowired
    public void setUserUtil(UserUtil userUtil) {
        this.userUtil = userUtil;
    }

    @Autowired
    public void setTrainerCreateRequestMapper(ToEntityMapper<TrainerCreateRequest, Trainer> trainerCreateRequestMapper) {
        this.trainerCreateRequestMapper = trainerCreateRequestMapper;
    }

    @Autowired
    public void setTrainerCreateResponseMapper(ToDTOMapper<Trainer, TrainerCreateResponse> trainerCreateResponseMapper) {
        this.trainerCreateResponseMapper = trainerCreateResponseMapper;
    }

    @Autowired
    public void setTrainerMapper(Mapper<Trainer, TrainerDTO> trainerMapper) {
        this.trainerMapper = trainerMapper;
    }
}

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TrainerServiceImpl implements TrainerService {

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
        if (trainer == null) throw new IllegalArgumentException();
        Trainer created = trainerCreateRequestMapper.toEntity(trainer);
        User user = created.getUser();
        userUtil.initializeUser(user);
        return trainerCreateResponseMapper.toDTO(trainerDao.save(created));
    }

    @Override
    public TrainerDTO update(TrainerDTO trainer) {
        Trainer updates = trainerMapper.toEntity(trainer);
        Trainer updated = trainerDao.findById(trainer.getId())
                .orElseThrow(NoSuchElementException::new);
        updated.setSpecialization(trainer.getSpecialization());
        updated.setUser(userUtil.updateUser(updated.getUser(), updates.getUser()));
        return trainerMapper.toDTO(trainerDao.save(updated));
    }

    @Override
    public Optional<TrainerDTO> findById(Long id) {
        return trainerDao.findById(id).map(trainerMapper::toDTO);
    }

    @Override
    public List<TrainerDTO> findAll() {
        return trainerDao.findAll()
                .stream()
                .map(trainerMapper::toDTO)
                .collect(Collectors.toList());
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

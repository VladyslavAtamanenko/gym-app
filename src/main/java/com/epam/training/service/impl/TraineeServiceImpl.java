package com.epam.training.service.impl;

import com.epam.training.dao.TraineeDao;
import com.epam.training.dto.TraineeCreateRequest;
import com.epam.training.dto.TraineeCreateResponse;
import com.epam.training.dto.TraineeDTO;
import com.epam.training.mapper.Mapper;
import com.epam.training.mapper.ToDTOMapper;
import com.epam.training.mapper.ToEntityMapper;
import com.epam.training.model.Trainee;
import com.epam.training.model.User;
import com.epam.training.service.TraineeService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TraineeServiceImpl implements TraineeService {

    private static final Log LOGGER = LogFactory.getLog(TraineeServiceImpl.class);

    private ToEntityMapper<TraineeCreateRequest, Trainee> traineeCreateRequestMapper;
    private ToDTOMapper<Trainee, TraineeCreateResponse> traineeCreateResponseMapper;
    private Mapper<Trainee, TraineeDTO> traineeMapper;
    private UserUtil userUtil;

    @Autowired
    private TraineeDao traineeDao;


    @Override
    public TraineeCreateResponse create(TraineeCreateRequest trainee) {
        if (trainee == null) {
            LOGGER.warn("Rejected trainee creation request because request body is null");
            throw new IllegalArgumentException();
        }
        LOGGER.debug("Creating trainee from request");
        Trainee created = traineeCreateRequestMapper.toEntity(trainee);
        User user = created.getUser();
        userUtil.initializeUser(user);
        Trainee saved = traineeDao.save(created);
        LOGGER.info("Trainee created successfully. traineeId=" + saved.getId());
        return traineeCreateResponseMapper.toDTO(saved);
    }

    @Override
    public TraineeDTO update(TraineeDTO trainee) {
        if (trainee == null) {
            LOGGER.warn("Rejected trainee update request because request body is null");
            throw new IllegalArgumentException();
        }
        LOGGER.debug("Updating trainee. traineeId=" + trainee.getId());
        Trainee updates = traineeMapper.toEntity(trainee);
        Trainee updated = traineeDao.findById(trainee.getId())
                .orElseThrow(() -> {
                    LOGGER.warn("Trainee update failed because trainee was not found. traineeId=" + trainee.getId());
                    return new NoSuchElementException();
                });
        updated.setAddress(updates.getAddress());
        updated.setDateOfBirth(updates.getDateOfBirth());
        updated.setUser(userUtil.updateUser(updated.getUser(), updates.getUser()));
        Trainee saved = traineeDao.save(updated);
        LOGGER.info("Trainee updated successfully. traineeId=" + saved.getId());
        return traineeMapper.toDTO(saved);
    }

    @Override
    public void delete(Long id) {
        LOGGER.debug("Deleting trainee. traineeId=" + id);
        traineeDao.delete(id);
    }

    @Override
    public Optional<TraineeDTO> findById(Long id) {
        Optional<TraineeDTO> result = traineeDao.findById(id).map(traineeMapper::toDTO);
        LOGGER.debug("Trainee lookup completed. traineeId=" + id + ", found=" + result.isPresent());
        return result;
    }

    @Override
    public List<TraineeDTO> findAll() {
        List<TraineeDTO> result = traineeDao.findAll().stream()
                .map(traineeMapper::toDTO)
                .collect(Collectors.toList());
        LOGGER.debug("Trainee list retrieved. count=" + result.size());
        return result;
    }

    @Autowired
    public void setUserUtil(UserUtil userUtil) {
        this.userUtil = userUtil;
    }

    @Autowired
    public void setTraineeCreateRequestMapper(ToEntityMapper<TraineeCreateRequest, Trainee> traineeCreateRequestMapper) {
        this.traineeCreateRequestMapper = traineeCreateRequestMapper;
    }

    @Autowired
    public void setTraineeCreateResponseMapper(ToDTOMapper<Trainee, TraineeCreateResponse> traineeCreateResponseMapper) {
        this.traineeCreateResponseMapper = traineeCreateResponseMapper;
    }

    @Autowired
    public void setTraineeMapper(Mapper<Trainee, TraineeDTO> traineeMapper) {
        this.traineeMapper = traineeMapper;
    }
}

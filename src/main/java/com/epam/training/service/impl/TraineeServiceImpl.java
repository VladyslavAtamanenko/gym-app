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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TraineeServiceImpl implements TraineeService {

    private ToEntityMapper<TraineeCreateRequest, Trainee> traineeCreateRequestMapper;
    private ToDTOMapper<Trainee, TraineeCreateResponse> traineeCreateResponseMapper;
    private Mapper<Trainee, TraineeDTO> traineeMapper;
    private UserUtil userUtil;

    @Autowired
    private TraineeDao traineeDao;


    @Override
    public TraineeCreateResponse create(TraineeCreateRequest trainee) {
        if (trainee == null) throw new IllegalArgumentException();
        Trainee created = traineeCreateRequestMapper.toEntity(trainee);
        User user = created.getUser();
        userUtil.initializeUser(user);
        return traineeCreateResponseMapper.toDTO(traineeDao.save(created));
    }

    @Override
    public TraineeDTO update(TraineeDTO trainee) {
        Trainee updates = traineeMapper.toEntity(trainee);
        Trainee updated = traineeDao.findById(trainee.getId())
                .orElseThrow(NoSuchElementException::new);
        updated.setAddress(updates.getAddress());
        updated.setDateOfBirth(updates.getDateOfBirth());
        updated.setUser(userUtil.updateUser(updated.getUser(), updates.getUser()));
        return traineeMapper.toDTO(traineeDao.save(updated));
    }

    @Override
    public void delete(Long id) {
        traineeDao.delete(id);
    }

    @Override
    public Optional<TraineeDTO> findById(Long id) {
        return traineeDao.findById(id).map(traineeMapper::toDTO);
    }

    @Override
    public List<TraineeDTO> findAll() {
        return traineeDao.findAll().stream()
                .map(traineeMapper::toDTO)
                .collect(Collectors.toList());
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

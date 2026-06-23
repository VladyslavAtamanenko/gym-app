package com.epam.training.service.impl;

import com.epam.training.dao.TrainingTypeDao;
import com.epam.training.dto.TrainingTypeDTO;
import com.epam.training.service.TrainingTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class TrainingTypeServiceImpl implements TrainingTypeService {

    private final TrainingTypeDao trainingTypeDao;

    @Autowired
    public TrainingTypeServiceImpl(TrainingTypeDao trainingTypeDao) {
        this.trainingTypeDao = trainingTypeDao;
    }

    @Override
    public List<TrainingTypeDTO> findAll() {
        return trainingTypeDao.findAll().stream()
                .map(t -> new TrainingTypeDTO(t.getId(), t.getName()))
                .collect(Collectors.toList());
    }
}

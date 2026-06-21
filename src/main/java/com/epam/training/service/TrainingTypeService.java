package com.epam.training.service;

import com.epam.training.dto.TrainingTypeDTO;

import java.util.List;

public interface TrainingTypeService {
    List<TrainingTypeDTO> findAll();
}

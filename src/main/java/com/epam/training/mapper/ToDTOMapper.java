package com.epam.training.mapper;

public interface ToDTOMapper<E, D> {
    D toDTO(E entity);
}

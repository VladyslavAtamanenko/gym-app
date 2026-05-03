package com.epam.training.mapper;

public interface ToEntityMapper<D, E> {
    E toEntity(D dto);
}

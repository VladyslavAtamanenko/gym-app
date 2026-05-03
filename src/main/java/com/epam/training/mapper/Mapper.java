package com.epam.training.mapper;

public interface Mapper<E, D> extends ToEntityMapper<D, E>, ToDTOMapper<E, D> {}

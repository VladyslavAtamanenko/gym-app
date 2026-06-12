package com.epam.training.service.impl;

import java.time.LocalDate;
import java.util.Collection;

final class ValidationUtil {

    private ValidationUtil() {
    }

    static void requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
    }

    static void requireNonNull(Object value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
    }

    static void requireDate(LocalDate value, String fieldName) {
        requireNonNull(value, fieldName);
    }

    static void requirePositive(Integer value, String fieldName) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException(fieldName + " must be positive");
        }
    }

    static void requireNotEmpty(Collection<?> value, String fieldName) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
    }
}

package com.epam.training.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ValidationUtil")
class ValidationUtilTest {

    @Test
    @DisplayName("requireNonBlank: throws IllegalArgumentException for null or blank string")
    void requireNonBlank_rejectsNullAndBlank() {
        assertThrows(IllegalArgumentException.class, () -> ValidationUtil.requireNonBlank(null, "field"));
        assertThrows(IllegalArgumentException.class, () -> ValidationUtil.requireNonBlank("  ", "field"));
    }

    @Test
    @DisplayName("requireNonBlank: accepts a non-blank string")
    void requireNonBlank_acceptsValue() {
        assertDoesNotThrow(() -> ValidationUtil.requireNonBlank("value", "field"));
    }

    @Test
    @DisplayName("requireNonNull: throws IllegalArgumentException for null value")
    void requireNonNull_rejectsNull() {
        assertThrows(IllegalArgumentException.class, () -> ValidationUtil.requireNonNull(null, "field"));
    }

    @Test
    @DisplayName("requireDate: throws IllegalArgumentException for null date")
    void requireDate_rejectsNull() {
        assertThrows(IllegalArgumentException.class, () -> ValidationUtil.requireDate(null, "date"));
    }

    @Test
    @DisplayName("requireDate: accepts a non-null LocalDate")
    void requireDate_acceptsValue() {
        assertDoesNotThrow(() -> ValidationUtil.requireDate(LocalDate.now(), "date"));
    }

    @Test
    @DisplayName("requirePositive: throws IllegalArgumentException for null, zero, or negative")
    void requirePositive_rejectsNullZeroAndNegative() {
        assertThrows(IllegalArgumentException.class, () -> ValidationUtil.requirePositive(null, "duration"));
        assertThrows(IllegalArgumentException.class, () -> ValidationUtil.requirePositive(0, "duration"));
        assertThrows(IllegalArgumentException.class, () -> ValidationUtil.requirePositive(-1, "duration"));
    }

    @Test
    @DisplayName("requirePositive: accepts a positive integer")
    void requirePositive_acceptsPositive() {
        assertDoesNotThrow(() -> ValidationUtil.requirePositive(1, "duration"));
    }

    @Test
    @DisplayName("requireNotEmpty: throws IllegalArgumentException for null or empty collection")
    void requireNotEmpty_rejectsNullAndEmpty() {
        assertThrows(IllegalArgumentException.class, () -> ValidationUtil.requireNotEmpty(null, "list"));
        assertThrows(IllegalArgumentException.class, () -> ValidationUtil.requireNotEmpty(Collections.emptyList(), "list"));
    }

    @Test
    @DisplayName("requireNotEmpty: accepts a non-empty collection")
    void requireNotEmpty_acceptsNonEmpty() {
        assertDoesNotThrow(() -> ValidationUtil.requireNotEmpty(List.of("item"), "list"));
    }
}

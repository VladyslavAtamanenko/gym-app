package com.epam.training.util;


import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;


import static org.junit.jupiter.api.Assertions.*;

public class UsernameGeneratorTest {

    private final UsernameGenerator generator = new UsernameGenerator();

    @Test
    @DisplayName("no collision: returns firstName.lastName preserving original case")
    void generate_noConflict_returnsBaseName() {
        String result = generator.generate("John", "Doe", Set.of());

        assertEquals("John.Doe", result);
    }


    @Test
    @DisplayName("one collision: appends numeric suffix 1")
    void generate_oneConflict_appendsSuffix1() {
        String result = generator.generate("John", "Doe", Set.of("John.Doe"));

        assertEquals("John.Doe1", result);
    }

    @Test
    @DisplayName("multiple collisions: increments counter until free slot found")
    void generate_multipleConflicts_findsNextFreeSlot() {
        Set<String> taken = Set.of("John.Doe", "John.Doe1", "John.Doe2");
        String result = generator.generate("John", "Doe", taken);

        assertEquals("John.Doe3", result);
    }

    @Test
    @DisplayName("large gap in taken suffixes: picks the first available number")
    void generate_gapInSuffixes_picksFirstAvailable() {

        Set<String> taken = Set.of("John.Doe", "John.Doe2");

        String result = generator.generate("John", "Doe", taken);

        assertEquals("John.Doe1", result);
    }

    @Test
    @DisplayName("empty existing set: always returns base name")
    void generate_emptySet_returnsBase() {
        assertEquals("Alice.Smith", generator.generate("Alice", "Smith", new HashSet<>()));
    }


    @Test
    @DisplayName("result is never null or blank")
    void generate_neverNullOrBlank() {
        String result = generator.generate("A", "B", Set.of());
        assertNotNull(result);
        assertFalse(result.isBlank());
    }
}


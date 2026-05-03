package com.epam.training.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordGeneratorTest {

    private static final String ALLOWED_CHARS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    private final PasswordGenerator generator = new PasswordGenerator();

    @Test
    @DisplayName("generate: returns non-null, non-blank string")
    void generate_notNullOrBlank() {
        assertNotNull(generator.generate());
        assertFalse(generator.generate().isBlank());
    }

    @Test
    @DisplayName("generate: length is always exactly 10")
    void generate_lengthIsAlwaysTen() {
        for (int i = 0; i < 20; i++) {
            assertEquals(10, generator.generate().length(),
                    "Password length must be exactly 10");
        }
    }

    @Test
    @DisplayName("generate: contains only alphanumeric characters [A-Za-z0-9]")
    void generate_onlyAllowedCharacters() {
        Set<Character> allowed = ALLOWED_CHARS.chars()
                .mapToObj(c -> (char) c)
                .collect(Collectors.toSet());

        for (int i = 0; i < 20; i++) {
            for (char c : generator.generate().toCharArray()) {
                assertTrue(allowed.contains(c),
                        "Unexpected character '" + c + "' found in generated password");
            }
        }
    }

    @RepeatedTest(5)
    @DisplayName("generate: successive calls produce different values (randomness check)")
    void generate_isRandom() {
        Set<String> results = IntStream.range(0, 10)
                .mapToObj(i -> generator.generate())
                .collect(Collectors.toSet());

        assertTrue(results.size() > 1,
                "10 consecutive passwords were all identical — generator is not random");
    }
}

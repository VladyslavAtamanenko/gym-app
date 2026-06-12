package com.epam.training.util;

import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class UsernameGenerator {

    public String generate(String firstName, String lastName, Set<String> existingUsernames) {
        String base = firstName + "." + lastName;

        if (!existingUsernames.contains(base)) {
            return base;
        }

        int counter = 1;
        String candidate;

        do {
            candidate = base + counter++;
        } while (existingUsernames.contains(candidate));

        return candidate;
    }
}
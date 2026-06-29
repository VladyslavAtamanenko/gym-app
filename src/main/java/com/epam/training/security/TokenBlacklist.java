package com.epam.training.security;

import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TokenBlacklist {

    private final Set<String> blacklisted = ConcurrentHashMap.newKeySet();

    public void add(String token) {
        blacklisted.add(token);
    }

    public boolean contains(String token) {
        return blacklisted.contains(token);
    }
}

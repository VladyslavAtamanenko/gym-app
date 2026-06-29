package com.epam.training.security;

import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Service
public class LoginAttemptService {

    private static final int MAX_ATTEMPTS = 3;
    private static final long BLOCK_DURATION_MS = 5 * 60 * 1000L;

    private final Map<String, Integer> failCount = new ConcurrentHashMap<>();
    private final Map<String, Long> lockTime = new ConcurrentHashMap<>();

    public boolean isBlocked(String username) {
        Long lockedAt = lockTime.get(username);
        if (lockedAt == null) return false;
        if (System.currentTimeMillis() - lockedAt > BLOCK_DURATION_MS) {
            failCount.remove(username);
            lockTime.remove(username);
            return false;
        }
        return true;
    }

    public void loginFailed(String username) {
        int count = failCount.merge(username, 1, Integer::sum);
        if (count >= MAX_ATTEMPTS) {
            lockTime.put(username, System.currentTimeMillis());
        }
    }

    public void loginSucceeded(String username) {
        failCount.remove(username);
        lockTime.remove(username);
    }
}

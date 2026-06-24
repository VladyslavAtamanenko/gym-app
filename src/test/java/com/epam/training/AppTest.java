package com.epam.training;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("local")
@DisplayName("Application context")
public class AppTest {

    @Test
    @DisplayName("loads Spring Boot application context without errors")
    void contextLoads() {
    }
}

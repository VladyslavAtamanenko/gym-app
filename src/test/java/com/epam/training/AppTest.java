package com.epam.training;

import com.epam.training.config.AppConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(AppConfig.class)
@DisplayName("Application context")
public class AppTest {

    @Test
    @DisplayName("loads Spring application context without errors")
    void contextLoads() {
    }
}

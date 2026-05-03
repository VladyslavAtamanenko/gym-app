package com.epam.training;

import com.epam.training.config.AppConfig;
import com.epam.training.config.StorageConfig;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig({AppConfig.class, StorageConfig.class})
public class AppTest {
    @Test
    void contextLoads() {
    }
}

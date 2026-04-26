package com.epam.training.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"com.epam.training.dao", "com.epam.training.service"})
public class AppConfig {
}

package com.epam.training.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:storage-data.properties")
public class PropertyConfig {
}

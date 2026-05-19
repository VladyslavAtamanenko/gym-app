package com.epam.training.config;

import com.epam.training.model.Trainee;
import com.epam.training.model.Trainer;
import com.epam.training.model.Training;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.HashMap;
import java.util.Map;


@Configuration
@Import(PropertyConfig.class)
public class StorageConfig {

    @Bean
    public Map<Long, Trainee> traineeMap (){
        return new HashMap<Long, Trainee>();
    }

    @Bean
    public Map<Long, Trainer> trainerMap (){
        return new HashMap<Long, Trainer>();
    }

    @Bean
    public Map<Long, Training> trainingMap (){
        return new HashMap<Long, Training>();
    }
}

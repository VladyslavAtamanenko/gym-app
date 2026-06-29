package com.epam.training.config;

import com.epam.training.dao.impl.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({TraineeDaoImpl.class, TrainerDaoImpl.class, TrainingDaoImpl.class, TrainingTypeDaoImpl.class, UserDaoImpl.class})
public class DaoTestConfig {
}

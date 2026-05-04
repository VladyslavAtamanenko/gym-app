package com.epam.training;

import com.epam.training.config.AppConfig;
import com.epam.training.config.StorageConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class App
{
    public static void main( String[] args ) {
        ApplicationContext context =
                new AnnotationConfigApplicationContext(AppConfig.class);
    }
}

package com.epam.training.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
public class PersistenceConfig {

    @Value("${DB_URL:jdbc:postgresql://localhost:5432/gym}")
    private String dbUrl;

    @Value("${DB_USERNAME:postgres}")
    private String dbUsername;

    @Value("${DB_PASSWORD:0000}")
    private String dbPassword;

    @Bean
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName("org.postgresql.Driver");
        ds.setUrl(dbUrl);
        ds.setUsername(dbUsername);
        ds.setPassword(dbPassword);
        return ds;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            DataSource dataSource) {

        LocalContainerEntityManagerFactoryBean emf =
                new LocalContainerEntityManagerFactoryBean();

        emf.setDataSource(dataSource);

        emf.setPackagesToScan("com.epam.training.model");

        HibernateJpaVendorAdapter vendorAdapter =
                new HibernateJpaVendorAdapter();

        vendorAdapter.setGenerateDdl(true);
        vendorAdapter.setShowSql(true);

        emf.setJpaVendorAdapter(vendorAdapter);

        Properties props = new Properties();

        props.put("hibernate.dialect",
                "org.hibernate.dialect.PostgreSQLDialect");

        props.put("hibernate.hbm2ddl.auto", "create-drop");

        props.put("hibernate.format_sql", "true");

        emf.setJpaProperties(props);

        return emf;
    }

    @Bean
    public PlatformTransactionManager transactionManager(
            EntityManagerFactory emf) {

        return new JpaTransactionManager(emf);
    }
}
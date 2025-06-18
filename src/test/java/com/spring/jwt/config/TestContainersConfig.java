package com.spring.jwt.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * TestContainers configuration for using real MySQL in tests
 */
@TestConfiguration
@Profile("test")
public class TestContainersConfig {

    /**
     * Creates a MySQL container for tests
     * @return MySQLContainer instance
     */
    @Bean
    @Primary
    public MySQLContainer<?> mySQLContainer() {
        MySQLContainer<?> container = new MySQLContainer<>(DockerImageName.parse("mysql:8.0"))
                .withDatabaseName("kartol_test")
                .withUsername("test")
                .withPassword("test")
                .withReuse(true);
        
        // Start the container
        container.start();
        
        // Override system properties to connect to the container
        System.setProperty("spring.datasource.url", container.getJdbcUrl());
        System.setProperty("spring.datasource.username", container.getUsername());
        System.setProperty("spring.datasource.password", container.getPassword());
        
        return container;
    }
} 
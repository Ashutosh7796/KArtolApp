package com.spring.jwt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * Configuration class for enabling scheduling in the application
 */
@Configuration
@EnableScheduling
public class SchedulingConfig {
    
    /**
     * Creates a task scheduler for dynamic scheduling of tasks
     * 
     * @return The task scheduler
     */
    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(10); // Adjust based on expected concurrent tasks
        scheduler.setThreadNamePrefix("dynamic-scheduler-");
        scheduler.setErrorHandler(throwable -> {
            System.err.println("Error in scheduled task: " + throwable.getMessage());
            throwable.printStackTrace();
        });
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setAwaitTerminationSeconds(60);
        return scheduler;
    }
} 
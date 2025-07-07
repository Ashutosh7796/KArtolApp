package com.spring.jwt.Exam.scheduler;

import com.spring.jwt.Exam.service.ExamSessionSchedulingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Dynamic scheduler for exam results
 * This scheduler checks for pending results hourly and initializes scheduled tasks on application startup
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DynamicExamResultScheduler {

    private final ExamSessionSchedulingService examSessionSchedulingService;
    
    /**
     * Hourly check for pending results
     * Runs every hour from 6 AM to 11 PM
     */
    @Scheduled(cron = "0 0 6-23 * * *")
    public void hourlyCheck() {
        LocalDateTime now = LocalDateTime.now();
        log.info("Running hourly check for pending exam results at {}", now);
        
        examSessionSchedulingService.checkForPendingResults();
    }
    
    /**
     * Daily maintenance check
     * Runs once a day at 6:30 AM to ensure all scheduled tasks are properly set up
     */
    @Scheduled(cron = "0 30 6 * * *")
    public void dailyMaintenance() {
        log.info("Running daily maintenance check for exam result scheduling at {}", LocalDateTime.now());
        
        examSessionSchedulingService.initializeScheduledTasks();
    }
} 
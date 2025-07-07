package com.spring.jwt.Exam.service;

import java.time.LocalDateTime;

/**
 * Service for dynamically scheduling exam result processing
 */
public interface ExamSessionSchedulingService {
    
    /**
     * Schedule a task to process exam results at the specified date and time
     * 
     * @param sessionId The ID of the exam session
     * @param resultDateTime The date and time when results should be processed
     * @return true if scheduling was successful, false otherwise
     */
    boolean scheduleExamResultProcessing(Integer sessionId, LocalDateTime resultDateTime);
    
    /**
     * Initialize scheduled tasks for any pending exam results on application startup
     * This ensures that any scheduled tasks are restored after application restart
     */
    void initializeScheduledTasks();
    
    /**
     * Check for any pending exam results that need to be processed
     * This will be run hourly to catch any missed scheduled tasks
     */
    void checkForPendingResults();
} 
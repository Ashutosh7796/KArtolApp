package com.spring.jwt.Exam.serviceImpl;

import com.spring.jwt.Exam.entity.ExamSession;
import com.spring.jwt.Exam.repository.ExamSessionRepository;
import com.spring.jwt.Exam.service.ExamResultService;
import com.spring.jwt.Exam.service.ExamSessionSchedulingService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

/**
 * Implementation of the ExamSessionSchedulingService
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ExamSessionSchedulingServiceImpl implements ExamSessionSchedulingService {

    private final ExamSessionRepository examSessionRepository;
    private final ExamResultService examResultService;
    private final TaskScheduler taskScheduler;
    
    // Store scheduled tasks by session ID for cancellation if needed
    private final Map<Integer, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();
    
    @PostConstruct
    public void init() {
        log.info("Initializing ExamSessionSchedulingService");
        initializeScheduledTasks();
    }
    
    @Override
    public boolean scheduleExamResultProcessing(Integer sessionId, LocalDateTime resultDateTime) {
        log.info("Scheduling exam result processing for session ID: {} at {}", sessionId, resultDateTime);
        
        try {
            // Cancel any existing scheduled task for this session
            cancelExistingTask(sessionId);
            
            // Schedule the new task
            Date targetDate = Date.from(resultDateTime.atZone(ZoneId.systemDefault()).toInstant());
            ScheduledFuture<?> scheduledTask = taskScheduler.schedule(() -> {
                log.info("Executing scheduled task for session ID: {}", sessionId);
                try {
                    ExamSession session = examSessionRepository.findById(sessionId).orElse(null);
                    if (session != null) {
                        examResultService.processExamSession(session);
                        log.info("Successfully processed exam session ID: {}", sessionId);
                    } else {
                        log.warn("Session ID: {} not found when executing scheduled task", sessionId);
                    }
                } catch (Exception e) {
                    log.error("Error processing scheduled exam result for session ID: {}", sessionId, e);
                } finally {
                    // Remove the task from the map once executed
                    scheduledTasks.remove(sessionId);
                }
            }, targetDate);
            
            // Store the scheduled task
            scheduledTasks.put(sessionId, scheduledTask);
            
            // Update the session with the result date
            ExamSession session = examSessionRepository.findById(sessionId).orElse(null);
            if (session != null) {
                session.setResultDate(resultDateTime);
                examSessionRepository.save(session);
                log.info("Updated session ID: {} with result date: {}", sessionId, resultDateTime);
                return true;
            } else {
                log.warn("Session ID: {} not found when scheduling", sessionId);
                return false;
            }
        } catch (Exception e) {
            log.error("Error scheduling exam result processing for session ID: {}", sessionId, e);
            return false;
        }
    }
    
    @Override
    public void initializeScheduledTasks() {
        log.info("Initializing scheduled tasks for pending exam results");
        
        try {
            // Find all sessions with result dates
            List<ExamSession> sessions = examSessionRepository.findAllWithResultDate();
            log.info("Found {} sessions with result dates", sessions.size());
            
            for (ExamSession session : sessions) {
                LocalDateTime resultDate = session.getResultDate();
                Integer sessionId = session.getSessionId();
                
                // Only schedule future dates
                if (resultDate != null && resultDate.isAfter(LocalDateTime.now())) {
                    log.info("Scheduling task for session ID: {} with result date: {}", sessionId, resultDate);
                    scheduleExamResultProcessing(sessionId, resultDate);
                } else if (resultDate != null) {
                    log.info("Session ID: {} has past result date: {}, processing immediately", sessionId, resultDate);
                    try {
                        examResultService.processExamSession(session);
                    } catch (Exception e) {
                        log.error("Error processing session ID: {}", sessionId, e);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error initializing scheduled tasks", e);
        }
    }
    
    /**
     * Scheduled task to check for pending results every hour
     * Disabled between midnight and 6:00 AM
     */
    @Scheduled(cron = "0 0 6-23 * * *") // Run at the start of every hour from 6 AM to 11 PM
    @Override
    public void checkForPendingResults() {
        LocalDateTime now = LocalDateTime.now();
        int hour = now.getHour();
        
        // Skip execution between midnight and 6:00 AM
        if (hour >= 0 && hour < 6) {
            log.info("Skipping pending results check during quiet hours (midnight to 6 AM)");
            return;
        }
        
        log.info("Checking for pending exam results at {}", now);
        
        try {
            List<ExamSession> readySessions = examSessionRepository.findByResultDateBeforeOrEqual(now);
            log.info("Found {} sessions with result date <= current time", readySessions.size());
            
            if (!readySessions.isEmpty()) {
                for (ExamSession session : readySessions) {
                    try {
                        log.info("Processing session ID: {} with result date: {}", 
                                session.getSessionId(), session.getResultDate());
                        examResultService.processExamSession(session);
                    } catch (Exception e) {
                        log.error("Error processing session ID: {}", session.getSessionId(), e);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error checking for pending results", e);
        }
    }
    
    /**
     * Cancel an existing scheduled task
     */
    private void cancelExistingTask(Integer sessionId) {
        ScheduledFuture<?> existingTask = scheduledTasks.get(sessionId);
        if (existingTask != null && !existingTask.isDone() && !existingTask.isCancelled()) {
            log.info("Cancelling existing scheduled task for session ID: {}", sessionId);
            existingTask.cancel(false);
            scheduledTasks.remove(sessionId);
        }
    }
} 
package com.spring.jwt.Exam.scheduler;

import com.spring.jwt.Exam.service.ExamResultService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduler for processing exam results automatically
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ExamResultScheduler {

    private final ExamResultService examResultService;
    
    /**
     * Scheduled task to process exam results
     * Runs every 5 minutes by default
     */
    @Scheduled(cron = "${app.scheduler.exam-results:0 */5 * * * *}")
    public void processReadyExamResults() {
        log.info("Starting scheduled exam result processing");
        try {
            int processed = examResultService.processReadyExamResults();
            if (processed > 0) {
                log.info("Scheduled task processed {} exam results", processed);
            } else {
                log.debug("No exam results to process in this scheduled run");
            }
        } catch (Exception e) {
            log.error("Error in scheduled exam result processing: {}", e.getMessage(), e);
        }
    }
} 
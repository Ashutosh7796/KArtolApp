package com.spring.jwt.Exam.scheduler;

import com.spring.jwt.Exam.entity.ExamSession;
import com.spring.jwt.Exam.repository.ExamSessionRepository;
import com.spring.jwt.Exam.service.ExamResultService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Scheduler for processing exam results automatically
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ExamResultScheduler {

    private final ExamResultService examResultService;
    private final ExamSessionRepository examSessionRepository;
    
    /**
     * Scheduled task to process exam results
     * Runs every 5 minutes by default
     */
    @Scheduled(cron = "${app.scheduler.exam-results:0 */5 * * * *}")
    public void processReadyExamResults() {
        log.info("Starting scheduled exam result processing at {}", LocalDateTime.now());
        try {
            // Directly check for ready sessions first for debugging
            LocalDateTime now = LocalDateTime.now();
            List<ExamSession> readySessions = examSessionRepository.findByResultDateBeforeOrEqual(now);
            log.info("Found {} sessions with resultDate <= {}", readySessions.size(), now);
            
            // Log details of each session for debugging
            if (!readySessions.isEmpty()) {
                for (ExamSession session : readySessions) {
                    log.info("Ready session: id={}, resultDate={}, userId={}, paperId={}",
                            session.getSessionId(),
                            session.getResultDate(),
                            session.getUser().getId(),
                            session.getPaper().getPaperId());
                }
            } else {
                log.info("No sessions found with resultDate in the past");
            }
            
            // Process the results
            int processed = examResultService.processReadyExamResults();
            if (processed > 0) {
                log.info("Scheduled task processed {} exam results", processed);
            } else {
                log.info("No exam results processed in this scheduled run");
            }
        } catch (Exception e) {
            log.error("Error in scheduled exam result processing: {}", e.getMessage(), e);
        }
    }
} 
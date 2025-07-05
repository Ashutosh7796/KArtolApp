package com.spring.jwt.Exam.controller;

import com.spring.jwt.Exam.entity.ExamSession;
import com.spring.jwt.Exam.repository.ExamSessionRepository;
import com.spring.jwt.Exam.service.ExamSessionSchedulingService;
import com.spring.jwt.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller for managing exam session scheduling
 */
@RestController
@RequestMapping("/api/v1/exam/session")
@RequiredArgsConstructor
@Slf4j
public class ExamSessionController {

    private final ExamSessionRepository examSessionRepository;
    private final ExamSessionSchedulingService examSessionSchedulingService;

    /**
     * Schedule result processing for an exam session
     * 
     * @param sessionId The ID of the exam session
     * @param resultDateTime The date and time when results should be processed
     * @return Response indicating success or failure
     */
    @PostMapping("/{sessionId}/schedule")
    public ResponseEntity<ResponseDto<Map<String, Object>>> scheduleExamResultProcessing(
            @PathVariable Integer sessionId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime resultDateTime) {
        
        log.info("Received request to schedule exam result processing for session ID: {} at {}", 
                sessionId, resultDateTime);
        
        ExamSession session = examSessionRepository.findById(sessionId).orElse(null);
        if (session == null) {
            return ResponseEntity.badRequest().body(
                    ResponseDto.error("Exam session not found", "No session found with ID: " + sessionId));
        }
        
        boolean scheduled = examSessionSchedulingService.scheduleExamResultProcessing(sessionId, resultDateTime);
        
        if (scheduled) {
            Map<String, Object> data = new HashMap<>();
            data.put("sessionId", sessionId);
            data.put("resultDateTime", resultDateTime);
            data.put("scheduled", true);
            
            return ResponseEntity.ok(ResponseDto.success(
                    "Exam result processing scheduled successfully", data));
        } else {
            return ResponseEntity.badRequest().body(
                    ResponseDto.error("Failed to schedule exam result processing", 
                            "Could not schedule processing for session ID: " + sessionId));
        }
    }
    
    /**
     * Manually trigger checking for pending results
     * 
     * @return Response indicating that the check was triggered
     */
    @PostMapping("/check-pending")
    public ResponseEntity<ResponseDto<Void>> checkPendingResults() {
        log.info("Manually triggering check for pending exam results");
        
        examSessionSchedulingService.checkForPendingResults();
        
        return ResponseEntity.ok(ResponseDto.success(
                "Check for pending exam results triggered", null));
    }
    
    /**
     * Manually reinitialize all scheduled tasks
     * 
     * @return Response indicating that reinitialization was triggered
     */
    @PostMapping("/reinitialize")
    public ResponseEntity<ResponseDto<Void>> reinitializeScheduledTasks() {
        log.info("Manually triggering reinitialization of scheduled tasks");
        
        examSessionSchedulingService.initializeScheduledTasks();
        
        return ResponseEntity.ok(ResponseDto.success(
                "Reinitialization of scheduled tasks triggered", null));
    }
} 
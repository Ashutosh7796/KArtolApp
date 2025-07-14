package com.spring.jwt.Exam.controller;

import com.spring.jwt.Exam.Dto.ExamResultDTO;
import com.spring.jwt.Exam.entity.ExamSession;
import com.spring.jwt.Exam.repository.ExamSessionRepository;
import com.spring.jwt.Exam.scheduler.ExamResultScheduler;
import com.spring.jwt.Exam.service.ExamResultService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Controller for exam results
 */
@RestController
@RequestMapping("/api/v1/exam-results")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Exam Results", description = "APIs for retrieving exam results")
public class ExamResultController {

    private final ExamResultService examResultService;
    private final ExamResultScheduler examResultScheduler;
    private final ExamSessionRepository examSessionRepository;
    
    @Operation(
            summary = "Get exam results by user ID",
            description = "Retrieves all exam results for a specific user",
            security = { @SecurityRequirement(name = "bearer-jwt") }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Results retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No results found for the user")
    })
    @GetMapping("/user/{userId}")
//    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER') or authentication.principal.id == #userId")
    @PermitAll
    public ResponseEntity<List<ExamResultDTO>> getResultsByUserId(@PathVariable Long userId) {
        List<ExamResultDTO> results = examResultService.getResultsByUserId(userId);
        return ResponseEntity.ok(results);
    }
    
    @Operation(
            summary = "Get exam results by paper ID",
            description = "Retrieves all exam results for a specific paper",
            security = { @SecurityRequirement(name = "bearer-jwt") }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Results retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No results found for the paper")
    })
    @GetMapping("/paper/{paperId}")
//    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @PermitAll
    public ResponseEntity<List<ExamResultDTO>> getResultsByPaperId(@PathVariable Integer paperId) {
        List<ExamResultDTO> results = examResultService.getResultsByPaperId(paperId);
        return ResponseEntity.ok(results);
    }
    
    @Operation(
            summary = "Get exam results by student class",
            description = "Retrieves all exam results for a specific student class",
            security = { @SecurityRequirement(name = "bearer-jwt") }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Results retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No results found for the class")
    })
    @GetMapping("/class/{studentClass}")
//    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @PermitAll
    public ResponseEntity<List<ExamResultDTO>> getResultsByStudentClass(@PathVariable String studentClass) {
        List<ExamResultDTO> results = examResultService.getResultsByStudentClass(studentClass);
        return ResponseEntity.ok(results);
    }
    
    @Operation(
            summary = "Manually process ready exam results",
            description = "Triggers the processing of exam sessions that have reached their result date",
            security = { @SecurityRequirement(name = "bearer-jwt") }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Processing completed successfully")
    })
    @PostMapping("/process")
//    @PreAuthorize("hasRole('ADMIN')")
    @PermitAll
    public ResponseEntity<String> processReadyResults() {
        int processed = examResultService.processReadyExamResults();
        return ResponseEntity.ok("Processed " + processed + " exam results");
    }
    
    @Operation(
            summary = "Debug scheduler execution",
            description = "Test endpoint to debug scheduler execution",
            security = { @SecurityRequirement(name = "bearer-jwt") }
    )
    @GetMapping("/debug/scheduler")
//    @PreAuthorize("hasRole('ADMIN')")
    @PermitAll
    public ResponseEntity<Map<String, Object>> debugScheduler() {
        log.info("Manually triggering scheduler for debugging");
        
        // Check raw database values
        List<Map<String, Object>> rawDates = examSessionRepository.findRawResultDates();
        log.info("Raw database values: {}", rawDates);
        
        // Check if there are sessions with result dates in the past
        LocalDateTime now = LocalDateTime.now();
        List<ExamSession> readySessions = examSessionRepository.findByResultDateBeforeOrEqual(now);
        
        // Get all sessions with result dates for debugging
        List<ExamSession> allWithResultDate = examSessionRepository.findAllWithResultDate();
        
        // Try to find sessions with the specific date mentioned (2025-07-04 22:37:00)
        LocalDateTime specificDate = LocalDateTime.of(2025, 7, 4, 22, 37, 0);
        List<ExamSession> specificDateSessions = examSessionRepository.findByExactResultDate(specificDate);
        
        // Manually trigger the scheduler
        examResultScheduler.processReadyExamResults();
        
        // Return debug info
        return ResponseEntity.ok(Map.of(
            "currentTime", now.toString(),
            "rawDatabaseValues", rawDates,
            "readySessionsCount", readySessions.size(),
            "readySessions", readySessions.stream().map(s -> Map.of(
                "sessionId", s.getSessionId(),
                "resultDate", s.getResultDate() != null ? s.getResultDate().toString() : "null",
                "userId", s.getUser().getId(),
                "paperId", s.getPaper().getPaperId()
            )).toList(),
            "allWithResultDateCount", allWithResultDate.size(),
            "allWithResultDate", allWithResultDate.stream().map(s -> Map.of(
                "sessionId", s.getSessionId(),
                "resultDate", s.getResultDate() != null ? s.getResultDate().toString() : "null"
            )).toList(),
            "specificDate", specificDate.toString(),
            "specificDateSessionsCount", specificDateSessions.size(),
            "specificDateSessions", specificDateSessions.stream().map(s -> Map.of(
                "sessionId", s.getSessionId(),
                "resultDate", s.getResultDate() != null ? s.getResultDate().toString() : "null"
            )).toList()
        ));
    }

    @Operation(
            summary = "Fix result date format",
            description = "Updates the result date format in the database",
            security = { @SecurityRequirement(name = "bearer-jwt") }
    )
    @PostMapping("/fix-date-format")
//    @PreAuthorize("hasRole('ADMIN')")
    @PermitAll
    public ResponseEntity<String> fixDateFormat() {
        log.info("Fixing result date format in the database");
        
        // Get the current date/time in the correct format
        LocalDateTime now = LocalDateTime.now();
        
        try {
            // Use a direct JDBC update to fix the date format
            int updated = examResultService.fixResultDateFormat();
            return ResponseEntity.ok("Fixed " + updated + " exam session dates");
        } catch (Exception e) {
            log.error("Error fixing date format: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Error fixing date format: " + e.getMessage());
        }
    }
} 
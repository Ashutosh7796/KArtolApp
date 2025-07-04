package com.spring.jwt.Exam.controller;

import com.spring.jwt.Exam.Dto.ExamResultDTO;
import com.spring.jwt.Exam.service.ExamResultService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER') or authentication.principal.id == #userId")
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
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
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
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> processReadyResults() {
        int processed = examResultService.processReadyExamResults();
        return ResponseEntity.ok("Processed " + processed + " exam results");
    }
} 
package com.spring.jwt.Question;

import com.spring.jwt.exception.PageNotFoundException;
import com.spring.jwt.exception.ResourceNotFoundException;
import com.spring.jwt.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Collection;
import java.util.stream.Collectors;

/////////////////////////////////////////////////////////////////////////////////////
//
//      File Name    : QuestionsController
//      Description  : to perform Questions CRUD Operations
//      Author       : Ashutosh Shedge
//      Date         : 28/04/2025
//
//////////////////////////////////////////////////////////////////////////////////
@RestController
@RequestMapping("/api/v1/questions")
@Tag(name = "Question Management", description = "APIs for managing questions")
@Validated
@RequiredArgsConstructor
public class QuestionController {

    private static final Logger log = LoggerFactory.getLogger(QuestionController.class);

    private final QuestionService questionService;

    @Operation(summary = "Create a new question", description = "Creates a new question with the provided details")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Question created successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping("/add")
    @PermitAll
    public ResponseEntity<ApiResponse<QuestionDTO>> createQuestion(
            @Parameter(description = "Question details", required = true)
            @Valid @RequestBody QuestionDTO questionDTO) {
        try {
            QuestionDTO created = questionService.createQuestion(questionDTO);
            return ResponseEntity.ok(ApiResponse.success("Question created successfully", created));
        } catch (Exception e) {
            log.error("Failed to create question: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.error(HttpStatus.BAD_REQUEST, "Failed to create question", e.getMessage()));
        }
    }

    @Operation(
            summary = "Create multiple questions in bulk",
            description = "Creates multiple questions with shared subject, userId, and studentClass. Returns a list of created questions."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Questions created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping("/add/bulk")
    @PermitAll
    public ResponseEntity<ApiResponse<List<QuestionDTO>>> createQuestionsBulk(
            @Parameter(description = "Bulk question details", required = true)
            @Valid @RequestBody BulkQuestionDTO bulkDTO) {
        try {
            List<QuestionDTO> created = questionService.createQuestionsBulk(bulkDTO);
            return ResponseEntity.ok(ApiResponse.success("Questions created successfully", created));
        } catch (Exception e) {
            log.error("Failed to create questions: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(
                    ApiResponse.error(HttpStatus.BAD_REQUEST, "Failed to create questions", e.getMessage())
            );
        }
    }
    @Operation(summary = "Get a question by ID", description = "Retrieves a question by its unique identifier")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Question found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Question not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/getById")
//    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN', 'TEACHER', 'STUDENT', 'PARENT') or isAuthenticated()")
    @PermitAll
    public ResponseEntity<ApiResponse<QuestionDTO>> getQuestionById(
            @Parameter(description = "Question ID", required = true, example = "1")
            @RequestParam @Min(1) Integer id) {
        try {
            QuestionDTO question = questionService.getQuestionById(id);
            return ResponseEntity.ok(ApiResponse.success("Question fetched successfully", question));
        } catch (QuestionNotFoundException e) {
            log.error("Question not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND, "Question not found", e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to fetch question: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST, "Failed to fetch question", e.getMessage()));
        }
    }

    @GetMapping("/all")
//    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('TEACHER')")
    @PermitAll
    public ResponseEntity<ApiResponse<Page<QuestionDTO>>> getAllQuestions(
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field", example = "questionId")
            @RequestParam(defaultValue = "questionId") String sortBy,
            @Parameter(description = "Sort direction (asc or desc)", example = "asc")
            @RequestParam(defaultValue = "asc") String direction) {
        try {
            log.info("Accessing /questions/all endpoint - requires authentication");
            
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated()) {
                log.info("User '{}' authenticated with authorities: {}", 
                    auth.getName(), 
                    auth.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.joining(", "))
                );
            } else {
                log.warn("No authentication found or user not authenticated for /questions/all");
            }

            Sort sort = direction.equalsIgnoreCase("asc") ? 
                Sort.by(sortBy).ascending() : 
                Sort.by(sortBy).descending();
            
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<QuestionDTO> questions = questionService.getAllQuestions(pageable);
            
            return ResponseEntity.ok(ApiResponse.success("All questions fetched successfully", questions));
        } catch (Exception e) {
            log.error("Error fetching all questions: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST, "Failed to fetch questions", e.getMessage()));
        }
    }
    @GetMapping("/allDeleted")
//    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('TEACHER')")
    @PermitAll
    public ResponseEntity<ApiResponse<Page<QuestionDTO>>> getAllQuestionsDeleted(
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field", example = "questionId")
            @RequestParam(defaultValue = "questionId") String sortBy,
            @Parameter(description = "Sort direction (asc or desc)", example = "asc")
            @RequestParam(defaultValue = "asc") String direction) {
        try {
            log.info("Accessing /questions/all endpoint - requires authentication");

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated()) {
                log.info("User '{}' authenticated with authorities: {}",
                        auth.getName(),
                        auth.getAuthorities().stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.joining(", "))
                );
            } else {
                log.warn("No authentication found or user not authenticated for /questions/all");
            }

            Sort sort = direction.equalsIgnoreCase("asc") ?
                    Sort.by(sortBy).ascending() :
                    Sort.by(sortBy).descending();

            Pageable pageable = PageRequest.of(page, size, sort);
            Page<QuestionDTO> questions = questionService.getAllQuestionsDeleted(pageable);

            return ResponseEntity.ok(ApiResponse.success("All questions fetched successfully", questions));
        } catch (Exception e) {
            log.error("Error fetching all questions: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST, "Failed to fetch questions", e.getMessage()));
        }
    }

    @PutMapping("/update")
    @PermitAll
    public ResponseEntity<ApiResponse<QuestionDTO>> updateQuestion(
            @Parameter(description = "Question ID to update", required = true, example = "1")
            @RequestParam @Min(1) Integer id,
            @Parameter(description = "Question data to update", required = true)
            @RequestBody QuestionUpdateDTO updatedQuestion // <-- Remove @Valid here
    ) {
        try {
            QuestionDTO updated = questionService.updateQuestion(id, updatedQuestion);
            return ResponseEntity.ok(ApiResponse.success("Question updated successfully", updated));
        } catch (QuestionNotFoundException e) {
            log.error("Question not found for update: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND, "Question not found", e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to update question: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST, "Failed to update question", e.getMessage()));
        }
    }


    @DeleteMapping("/hardDelete")
//    @PreAuthorize("hasAuthority('ADMIN')")
    @PermitAll
    public ResponseEntity<ApiResponse<Void>> deleteQuestion(
            @Parameter(description = "Question ID to delete", required = true, example = "1")
            @RequestParam @Min(1) Integer id) {
        try {
            questionService.deleteQuestion(id);
            return ResponseEntity.ok(ApiResponse.success("Question deleted successfully"));
        } catch (QuestionNotFoundException e) {
            log.error("Question not found for deletion: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND, "Question not found", e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to delete question: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST, "Failed to delete question", e.getMessage()));
        }
    }

    @GetMapping("/user")
//    @PreAuthorize("isAuthenticated()")
    @PermitAll
    public ResponseEntity<ApiResponse<Page<QuestionDTO>>> getQuestionsByUserId(
            @Parameter(description = "User ID", required = true, example = "1")
            @RequestParam @Min(1) Integer userId,
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field", example = "questionId")
            @RequestParam(defaultValue = "questionId") String sortBy,
            @Parameter(description = "Sort direction (asc or desc)", example = "asc")
            @RequestParam(defaultValue = "asc") String direction) {
        try {
            Sort sort = direction.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() : 
                Sort.by(sortBy).descending();
            
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<QuestionDTO> questions = questionService.getQuestionsByUserId(userId, pageable);
            
            return ResponseEntity.ok(ApiResponse.success("Questions fetched for userId " + userId, questions));
        } catch (QuestionNotFoundException e) {
            log.error("No questions found for user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND, "No questions found", e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to fetch questions by userId: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST, "Failed to fetch questions by userId", e.getMessage()));
        }
    }
    @GetMapping("/userIdDescriptive")
    @PermitAll
    public ResponseEntity<ApiResponse<Page<QuestionDTO>>> getQuestionsByUserIdAndDescriptive(
            @Parameter(description = "User ID", required = true, example = "1")
            @RequestParam @Min(1) Integer userId,
            @Parameter(description = "Is question descriptive? true or false", required = true, example = "false")
            @RequestParam boolean isDescriptive,
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field", example = "questionId")
            @RequestParam(defaultValue = "questionId") String sortBy,
            @Parameter(description = "Sort direction (asc or desc)", example = "desc")
            @RequestParam(defaultValue = "desc") String direction) {

        try {
            Sort sort = direction.equalsIgnoreCase("asc") ?
                    Sort.by(sortBy).ascending() :
                    Sort.by(sortBy).descending();

            Pageable pageable = PageRequest.of(page, size, sort);
            Page<QuestionDTO> questions = questionService.getQuestionsByUserIdAndDescriptive(userId, isDescriptive, pageable);

            return ResponseEntity.ok(ApiResponse.success("Questions fetched for userId " + userId, questions));
        } catch (ResourceNotFoundException e) {
            log.error("No questions found for user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND, "No questions found", e.getMessage()));
        } catch (PageNotFoundException e) {
            log.error("Requested page not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND, "Page not found", e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to fetch questions by userId: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST, "Failed to fetch questions by userId", e.getMessage()));
        }
    }

    @GetMapping("/search")
    @PermitAll
    public ResponseEntity<ApiResponse<Page<QuestionDTO>>> getQuestionsBySubTypeLevelMarks(
            @Parameter(description = "Subject", example = "Mathematics")
            @RequestParam(required = false) String subject,
            @Parameter(description = "Question type", example = "MCQ")
            @RequestParam(required = false) String type,
            @Parameter(description = "Difficulty level", example = "Medium")
            @RequestParam(required = false) String level,
            @Parameter(description = "Marks", example = "5")
            @RequestParam(required = false) String marks,
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field", example = "questionId")
            @RequestParam(defaultValue = "questionId") String sortBy,
            @Parameter(description = "Sort direction (asc or desc)", example = "asc")
            @RequestParam(defaultValue = "asc") String direction) {
        try {
            Map<String, String> filters = new HashMap<>();
            if (subject != null) filters.put("subject", subject);
            if (type != null) filters.put("type", type);
            if (level != null) filters.put("level", level);
            if (marks != null) filters.put("marks", marks);
            
            Sort sort = direction.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() : 
                Sort.by(sortBy).descending();
            
            Pageable pageable = PageRequest.of(page, size, sort);
            
            Page<QuestionDTO> questions = questionService.searchQuestions(filters, pageable);
            
            return ResponseEntity.ok(ApiResponse.success("Questions fetched by criteria", questions));
        } catch (QuestionNotFoundException e) {
            log.error("No questions found for criteria: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND, "No questions found", e.getMessage()));
        } catch (InvalidQuestionException e) {
            log.error("Invalid search criteria: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST, "Invalid search criteria", e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to fetch questions by criteria: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST, "Failed to fetch questions by criteria", e.getMessage()));
        }
    }
    @PutMapping("/delete-statusChange")
//@PreAuthorize("hasAuthority('ADMIN')")
    @PermitAll
    public ResponseEntity<ApiResponse<Void>> updateQuestionDeletedStatus(
            @Parameter(description = "Question ID to update deleted status", required = true, example = "1")
            @RequestParam Integer id,
            @RequestBody QuestionDeletedDTO dto) {
        try {
            questionService.updateDeletedStatus(id, dto.isDeleted());
            return ResponseEntity.ok(ApiResponse.success("Deleted status updated successfully"));
        } catch (ResourceNotFoundException e) {
            log.error("Question not found for update deleted status: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND, "Question not found", e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to update deleted status: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST, "Failed to update deleted status", e.getMessage()));
        }
    }

    @PutMapping("/soft-delete")
//@PreAuthorize("hasAuthority('ADMIN')")
    @PermitAll
    public ResponseEntity<ApiResponse<Void>> toggleQuestionDeletedStatus(
            @Parameter(description = "Question ID to toggle deleted status", required = true, example = "1")
            @RequestParam Integer id) {
        try {
            questionService.toggleDeletedStatus(id);
            return ResponseEntity.ok(ApiResponse.success("Deleted status toggled successfully"));
        } catch (ResourceNotFoundException e) {
            log.error("Question not found for toggle deleted status: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND, "Question not found", e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to toggle deleted status: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST, "Failed to toggle deleted status", e.getMessage()));
        }
    }

}
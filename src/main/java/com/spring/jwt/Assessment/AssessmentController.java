package com.spring.jwt.Assessment;

import com.spring.jwt.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/////////////////////////////////////////////////////////////////////////////////////
//
//      File Name    : AssessmentController
//      Description  : to perform assessments actions
//      Author       : Ashutosh Shedge
//      Date         : 28/04/2025
//
//////////////////////////////////////////////////////////////////////////////////
@RestController
@RequestMapping("/api/v1/assessments")
@Tag(name = "Assessment Management", description = "APIs for managing assessments")
@Validated
@RequiredArgsConstructor
public class AssessmentController {

    private final AssessmentService assessmentService;

    @Operation(summary = "Create a new assessment", description = "Creates a new assessment with questions")
    @PostMapping("/create")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('TEACHER')")
    public ResponseEntity<ApiResponse<AssessmentDTO>> createAssessment(
            @Parameter(description = "Assessment details", required = true)
            @Valid @RequestBody AssessmentDTO assessmentDTO
    ) {
        try {
            AssessmentDTO result = assessmentService.createAssessment(assessmentDTO);
            return ResponseEntity.ok(ApiResponse.success("Assessment created successfully", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(HttpStatus.BAD_REQUEST, "Failed to create assessment", e.getMessage()));
        }
    }

    @Operation(summary = "Get an assessment by ID", description = "Retrieves an assessment by its unique identifier")
    @GetMapping("/getById")
    //@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('TEACHER') or hasAuthority('STUDENT')")
    public ResponseEntity<ApiResponse<AssessmentDTO>> getAssessmentById(
            @Parameter(description = "Assessment ID", required = true, example = "1")
            @RequestParam @Min(1) Integer id
    ) {
        try {
            AssessmentDTO dto = assessmentService.getAssessmentById(id);
            return ResponseEntity.ok(ApiResponse.success("Assessment fetched successfully", dto));
        } catch (AssessmentNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND, "Assessment not found", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST, "Failed to fetch assessment", e.getMessage()));
        }
    }

    @Operation(summary = "Get all assessments with pagination", description = "Retrieves all assessments with pagination and sorting options")
    @GetMapping("/all")
    //@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('TEACHER')")
    public ResponseEntity<ApiResponse<Page<AssessmentDTO>>> getAllAssessments(
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field", example = "assessmentId,title,assessmentDate,subject,")
            @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction (asc or desc)", example = "asc")
            @RequestParam(defaultValue = "asc") String direction
    ) {
        try {
            Sort sort = direction.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();

            Pageable pageable = PageRequest.of(page, size, sort);
            Page<AssessmentDTO> result = assessmentService.getAllAssessments(pageable);

            return ResponseEntity.ok(ApiResponse.success("All assessments fetched successfully", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST, "Failed to fetch assessments", e.getMessage()));
        }
    }

    @Operation(summary = "Update an assessment", description = "Updates an existing assessment by ID")
    @PatchMapping("/update")
    //@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('TEACHER')")
    public ResponseEntity<ApiResponse<AssessmentDTO>> updateAssessment(
            @Parameter(description = "Assessment ID", required = true, example = "1")
            @RequestParam @Min(1) Integer id,
            @Parameter(description = "Assessment details to update", required = true)
            @Valid @RequestBody AssessmentDTO assessmentDTO
    ) {
        try {
            AssessmentDTO updated = assessmentService.updateAssessment(id, assessmentDTO);
            return ResponseEntity.ok(ApiResponse.success("Assessment updated successfully", updated));
        } catch (AssessmentNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND, "Assessment not found", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST, "Failed to update assessment", e.getMessage()));
        }
    }

    @Operation(summary = "Delete an assessment", description = "Deletes an assessment by ID")
    @DeleteMapping("/delete")
   // @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteAssessment(
            @Parameter(description = "Assessment ID", required = true, example = "1")
            @RequestParam @Min(1) Integer id
    ) {
        try {
            assessmentService.deleteAssessment(id);
            return ResponseEntity.ok(ApiResponse.success("Assessment deleted successfully"));
        } catch (AssessmentNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND, "Assessment not found", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST, "Failed to delete assessment", e.getMessage()));
        }
    }

    @Operation(summary = "Add a question to an assessment", description = "Adds a question to an existing assessment")
    @PostMapping("/{assessmentId}/questions")
    //@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('TEACHER')")
    public ResponseEntity<ApiResponse<AssessmentDTO>> addQuestionToAssessment(
            @Parameter(description = "Assessment ID", required = true, example = "1")
            @PathVariable @Min(1) Integer assessmentId,
            @Parameter(description = "Question details", required = true)
            @Valid @RequestBody AssessmentDTO.AssessmentQuestionDTO questionDTO
    ) {
        try {
            AssessmentDTO updated = assessmentService.addQuestionToAssessment(assessmentId, questionDTO);
            return ResponseEntity.ok(ApiResponse.success("Question added to assessment successfully", updated));
        } catch (AssessmentNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND, "Assessment not found", e.getMessage()));
        } catch (DuplicateQuestionInSetException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST, "Question already exists in assessment", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST, "Failed to add question to assessment", e.getMessage()));
        }
    }

    @Operation(summary = "Remove a question from an assessment", description = "Removes a question from an existing assessment")
    @DeleteMapping("/{assessmentId}/questions/{questionId}")
   // @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('TEACHER')")
    public ResponseEntity<ApiResponse<AssessmentDTO>> removeQuestionFromAssessment(
            @Parameter(description = "Assessment ID", required = true, example = "1")
            @PathVariable @Min(1) Integer assessmentId,
            @Parameter(description = "Question ID", required = true, example = "1")
            @PathVariable @Min(1) Integer questionId
    ) {
        try {
            AssessmentDTO updated = assessmentService.removeQuestionFromAssessment(assessmentId, questionId);
            return ResponseEntity.ok(ApiResponse.success("Question removed from assessment successfully", updated));
        } catch (AssessmentNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND, "Assessment not found", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST, "Failed to remove question from assessment", e.getMessage()));
        }
    }

    @Operation(summary = "Get assessments by user ID (creator)", description = "Retrieves assessments created by a specific user")
    @GetMapping("/user/{userId}")
   // @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('TEACHER') or (hasAuthority('USER') and #userId == authentication.principal.userId)")
    public ResponseEntity<ApiResponse<Page<AssessmentDTO>>> getAssessmentsByUserId(
            @Parameter(description = "User ID", required = true, example = "1")
            @PathVariable @Min(1) Integer userId,
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field", example = "id")
            @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction (asc or desc)", example = "asc")
            @RequestParam(defaultValue = "asc") String direction
    ) {
        try {
            Sort sort = direction.equalsIgnoreCase("asc") ? 
                Sort.by(sortBy).ascending() : 
                Sort.by(sortBy).descending();
            
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<AssessmentDTO> result = assessmentService.getAssessmentsByUserId(userId, pageable);
            
            return ResponseEntity.ok(ApiResponse.success("Assessments fetched by user successfully", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST, "Failed to fetch assessments by user", e.getMessage()));
        }
    }

    @Operation(summary = "Search assessments", description = "Searches for assessments based on various criteria")
    @GetMapping("/search")
   // @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('TEACHER')")
    public ResponseEntity<ApiResponse<Page<AssessmentDTO>>> searchAssessments(
            @Parameter(description = "Subject", example = "Mathematics")
            @RequestParam(required = false) String subject,
            @Parameter(description = "Title (partial match)", example = "Midterm")
            @RequestParam(required = false) String title,
            @Parameter(description = "Creator ID", example = "1")
            @RequestParam(required = false) Integer createdBy,
            @Parameter(description = "Is active", example = "true")
            @RequestParam(required = false) Boolean isActive,
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field", example = "id")
            @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction (asc or desc)", example = "asc")
            @RequestParam(defaultValue = "asc") String direction
    ) {
        try {
            Map<String, String> filters = new HashMap<>();
            if (subject != null) filters.put("subject", subject);
            if (title != null) filters.put("title", title);
            if (createdBy != null) filters.put("createdBy", createdBy.toString());
            if (isActive != null) filters.put("isActive", isActive.toString());
            
            Sort sort = direction.equalsIgnoreCase("asc") ? 
                Sort.by(sortBy).ascending() : 
                Sort.by(sortBy).descending();
            
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<AssessmentDTO> result = assessmentService.searchAssessments(filters, pageable);
            
            return ResponseEntity.ok(ApiResponse.success("Assessments found by search criteria", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST, "Failed to search assessments", e.getMessage()));
        }
    }
}
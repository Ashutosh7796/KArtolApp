package com.spring.jwt.Assessment;

import com.spring.jwt.entity.enum01.QType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Data Transfer Object for Assessment operations
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Assessment Data Transfer Object")
public class AssessmentDTO {
    
    @Schema(description = "Unique identifier of the assessment", example = "1")
    private Integer id;
    
    @Schema(description = "Set number for grouping related assessments", example = "1001")
    private Long setNumber;
    
    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    @Schema(description = "Title of the assessment", example = "Mathematics Midterm Exam", required = true)
    private String title;
    
    @NotBlank(message = "Description is required")
    @Size(min = 10, max = 500, message = "Description must be between 10 and 500 characters")
    @Schema(description = "Description of the assessment", example = "Midterm examination covering algebra and calculus", required = true)
    private String description;
    
    @Schema(description = "Assessment date", example = "2023-06-15")
    private String assessmentDate;
    
    @Schema(description = "Duration in minutes", example = "60")
    private String duration;
    
    @Schema(description = "Start time", example = "09:00")
    private String startTime;
    
    @Schema(description = "End time", example = "10:30")
    private String endTime;
    
    @Schema(description = "User ID of the creator", example = "1")
    private Integer userId;
    
    @NotNull(message = "Total marks is required")
    @Min(value = 1, message = "Total marks must be at least 1")
    @Schema(description = "Total marks for the assessment", example = "100", required = true)
    private Integer totalMarks;
    
    @Schema(description = "Pass marks for the assessment", example = "40")
    private Integer passMarks;
    
    @NotBlank(message = "Subject is required")
    @Schema(description = "Subject of the assessment", example = "Mathematics", required = true)
    private String subject;
    
    @Schema(description = "ID of the user who created the assessment", example = "1")
    private Integer createdBy;
    
    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;
    
    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;
    
    @Schema(description = "Flag indicating if the assessment is active", example = "true")
    private Boolean isActive;
    
    @Schema(description = "List of question IDs included in this assessment")
    private List<Integer> questionIds;
    
    @Valid
    @Schema(description = "List of questions with detailed information")
    private List<AssessmentQuestionDTO> questions;
    
    /**
     * Data Transfer Object for Assessment Question
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Assessment Question Data Transfer Object")
    public static class AssessmentQuestionDTO {
        
        @Schema(description = "Unique identifier of the assessment question", example = "1")
        private Integer id;
        
        @NotNull(message = "Question ID is required")
        @Schema(description = "ID of the question", example = "1", required = true)
        private Integer questionId;
        
        @Schema(description = "ID of the assessment", example = "1")
        private Integer assessmentId;
        
        @Schema(description = "Order number of the question in the assessment", example = "1")
        private Integer questionOrder;
        
        @Schema(description = "Points assigned to the question", example = "10")
        private Integer points;
        
        @Schema(description = "Order number of the question (alternative name)", example = "1")
        private Integer orderNumber;
        
        @Schema(description = "Marks assigned to the question (alternative name)", example = "10")
        private Integer marks;
        
        @Schema(description = "Question text", example = "What is the capital of France?")
        private String questionText;
        
        @Schema(description = "Question type", example = "MCQ")
        private QType questionType;
    }
}
package com.spring.jwt.Exam.Dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data Transfer Object for Paper operations")
public class PaperDTO1 {

    @Schema(description = "Unique identifier of the paper", example = "1")
    private Integer paperId;

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 255, message = "Title must be between 3 and 255 characters")
    @Schema(description = "Title of the paper", example = "Midterm Exam", required = true)
    private String title;

    @NotBlank(message = "Description is required")
    @Size(min = 5, max = 1000, message = "Description must be between 5 and 1000 characters")
    @Schema(description = "Description of the paper", example = "Midterm exam for 10th grade", required = true)
    private String description;

    @NotNull(message = "Start time is required")
    @Schema(description = "Start time of the paper", example = "2025-06-23T10:00:00", required = true)
    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    @Schema(description = "End time of the paper", example = "2025-06-23T12:00:00", required = true)
    private LocalDateTime endTime;

    private LocalDateTime paperEndTime;

    @NotNull(message = "isLive flag is required")
    @Schema(description = "Whether the paper is currently live", example = "true", required = true)
    private Boolean isLive;

    @NotBlank(message = "Student class is required")
    @Schema(description = "Class for which the paper is intended", example = "10th Grade", required = true)
    private String studentClass;

    @NotNull(message = "Questions list cannot be null")
    @Size(min = 1, message = "At least one question ID must be provided")
    @Schema(description = "List of question IDs included in the paper", example = "[101, 102, 103]", required = true)
    private List<@NotNull(message = "Question ID cannot be null") Integer> questions;


    @NotNull(message = "resultDate is required")
    @Schema(description = "resultDate of the paper", example = "2025-06-23T12:00:00", required = true)
    private LocalDateTime resultDate;

    private Integer paperPatternId;

    private String patternName;
    @Schema(description = "List of negative marks per question")
    private List<NegativeMarksDTO> negativeMarksList;
}
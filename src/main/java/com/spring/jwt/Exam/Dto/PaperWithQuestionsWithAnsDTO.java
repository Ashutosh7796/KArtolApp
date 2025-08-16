package com.spring.jwt.Exam.Dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import com.spring.jwt.Question.QuestionDTO;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO for Paper with full Question details")
public class PaperWithQuestionsWithAnsDTO {

    @Schema(description = "Unique identifier of the paper", example = "1")
    private Integer paperId;

    @Schema(description = "Title of the paper", example = "Midterm Exam")
    private String title;

    @Schema(description = "Description of the paper", example = "Midterm exam for 10th grade")
    private String description;

    @Schema(description = "Start time of the paper", example = "2025-06-23T10:00:00")
    private LocalDateTime startTime;

    @Schema(description = "End time of the paper", example = "2025-06-23T12:00:00")
    private LocalDateTime endTime;

    @Schema(description = "Whether the paper is currently live", example = "true")
    private Boolean isLive;

    @Schema(description = "Class for which the paper is intended", example = "10th Grade")
    private String studentClass;

    @Schema(description = "List of question details included in the paper")
    private List<QuestionDTO> questions;

    @Schema(description = "isDescriptive type ture")
    private boolean isDescriptive;

    private LocalDateTime paperEndTime;
}

package com.spring.jwt.Question;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.spring.jwt.entity.enum01.QType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data Transfer Object for Question operations")
public class QuestionDTO {

    @Schema(description = "Unique identifier of the question", example = "1")
    private Integer questionId;

    @NotBlank(message = "Question text is required")
    @Size(min = 5, max = 1000, message = "Question text must be between 5 and 1000 characters")
    @Schema(description = "The text of the question", example = "What is the capital of France?", required = true)
    private String questionText;

    @NotNull(message = "Question type is required")
    @Schema(description = "Type of question (e.g., MCQ, Essay)", example = "MCQ", required = true)
    private QType type;

    @NotBlank(message = "Subject is required")
    @Schema(description = "Subject the question belongs to", example = "Geography", required = true)
    private String subject;
    @NotBlank
    private String unit;
    @NotBlank
    private String chapter;
    @NotBlank
    private String topic;

    @NotBlank(message = "Level is required")
    @Schema(description = "Difficulty level of the question", example = "Medium", required = true)
    private String level;

    @NotNull(message = "Marks are required")
    @Min(value = 1, message = "Marks must be at least 1")
    @Schema(description = "Marks assigned to the question", example = "5", required = true)
    private Integer marks;

    @NotNull(message = "User ID is required")
    @Schema(description = "ID of the user who created the question", example = "1", required = true)
    private Integer userId;

    @Size(min = 1, max = 500, message = "Option 1 must be between 1 and 500 characters")
    @Schema(description = "First option for multiple choice questions", example = "Paris")
    private String option1;

    @Size(min = 1, max = 500, message = "Option 2 must be between 1 and 500 characters")
    @Schema(description = "Second option for multiple choice questions", example = "London")
    private String option2;

    @Schema(description = "Third option for multiple choice questions", example = "Berlin")
    private String option3;

    @Schema(description = "Fourth option for multiple choice questions", example = "Rome")
    private String option4;

    @Schema(description = "Correct answer to the question", example = "Paris", required = true)
    private String answer;

    @JsonProperty("studentClass")
    @NotBlank(message = "Student class is required")
    private String StudentClass;

    @JsonProperty("descriptive")
    private boolean isDescriptive;

    @JsonProperty("MultiOptions")
    private boolean isMultiOptions;

    @Schema(description = "Correct answer to hint And Sol", example = "Paris")
    private String hintAndSol;


}
package com.spring.jwt.Question;

import com.spring.jwt.entity.enum01.QType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
    
    @NotBlank(message = "Question type is required")
    @Schema(description = "Type of question (e.g., MCQ, Essay)", example = "MCQ", required = true)
    private QType type;
    
    @NotBlank(message = "Subject is required")
    @Schema(description = "Subject the question belongs to", example = "Geography", required = true)
    private String subject;
    
    @NotBlank(message = "Level is required")
    @Schema(description = "Difficulty level of the question", example = "Medium", required = true)
    private String level;
    
    @NotBlank(message = "Marks are required")
    @Pattern(regexp = "^[0-9]+(\\.[0-9]+)?$", message = "Marks must be a valid number")
    @Schema(description = "Marks assigned to the question", example = "5", required = true)
    private String marks;
    
    @NotNull(message = "User ID is required")
    @Schema(description = "ID of the user who created the question", example = "1", required = true)
    private Integer userId;
    
    @NotBlank(message = "Option 1 is required")
    @Size(min = 1, max = 500, message = "Option 1 must be between 1 and 500 characters")
    @Schema(description = "First option for multiple choice questions", example = "Paris", required = true)
    private String option1;
    
    @NotBlank(message = "Option 2 is required")
    @Size(min = 1, max = 500, message = "Option 2 must be between 1 and 500 characters")
    @Schema(description = "Second option for multiple choice questions", example = "London", required = true)
    private String option2;
    
    @Size(max = 500, message = "Option 3 must be less than 500 characters")
    @Schema(description = "Third option for multiple choice questions", example = "Berlin")
    private String option3;
    
    @Size(max = 500, message = "Option 4 must be less than 500 characters")
    @Schema(description = "Fourth option for multiple choice questions", example = "Rome")
    private String option4;
    
    @NotBlank(message = "Answer is required")
    @Schema(description = "Correct answer to the question", example = "Paris", required = true)
    private String answer;

    @NotBlank(message = "Student Class")
    @Schema(description = "Student Class", example = "Paris", required = true)
    private String StudentClass;


    @Schema(description = "Correct answer to hint And Sol", example = "Paris")
    private String hintAndSol;

    @NotBlank(message = "DESCRIPTIVE or not")
    @Schema(description = "DESCRIPTIVE ture if Q is DESCRIPTIVE", example = "Paris", required = true)
    private boolean isDescriptive;
}

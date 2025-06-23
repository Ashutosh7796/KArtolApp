package com.spring.jwt.Question;

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
public class SingleQuestionDTO {
    @NotBlank
    @Size(min = 5, max = 1000)
    @Schema(description = "The text of the question", example = "What is the capital of France?")
    private String questionText;

    @NotBlank
    @Schema(description = "Type of question (e.g., MCQ, Essay)", example = "MCQ")
    private String type;

    @NotBlank
    @Schema(description = "Difficulty level", example = "Medium")
    private String level;

    @NotBlank
    @Pattern(regexp = "^[0-9]+(\\.[0-9]+)?$")
    @Schema(description = "Marks", example = "5")
    private String marks;

    @NotBlank
    @Size(min = 1, max = 500)
    private String option1;

    @NotBlank
    @Size(min = 1, max = 500)
    private String option2;

    @Size(max = 500)
    private String option3;

    @Size(max = 500)
    private String option4;

    @NotBlank
    private String answer;

    // getters and setters
}
package com.spring.jwt.Question;

import com.spring.jwt.entity.enum01.QType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Lob;
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

    @NotNull
    @Schema(description = "Type of question (e.g., MCQ, Essay)", example = "MCQ")
    private QType type;

    @NotBlank
    @Schema(description = "Difficulty level", example = "Medium")
    private String level;

    @NotBlank
    @Schema(description = "Marks", example = "5")
    private Integer marks;

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


    private String answer;

    @Schema(description = "Correct answer to hint And Sol", example = "1")
    private String hintAndSol;

    @NotNull
    @Schema(description = "DESCRIPTIVE ture if Q is DESCRIPTIVE", example = "ture", required = true)
    private boolean isDescriptive;

    @NotNull
    @Schema(description = "isMultiOptions ture if Q is MultiOptions", example = "ture", required = true)
    private boolean isMultiOptions;

    // getters and setters
}
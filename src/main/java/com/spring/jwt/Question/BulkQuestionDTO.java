package com.spring.jwt.Question;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkQuestionDTO {

    @NotBlank
    @Schema(description = "Subject for all questions", example = "Geography")
    private String subject;

    @NotBlank
    @Schema(description = "UNIT for all questions", example = "UNIT 1")
    private String unit;

    @NotBlank
    private String chapter;

    @NotBlank
    private String topic;

    @NotNull
    @Schema(description = "User ID for all questions", example = "1")
    private Integer userId;

    @NotBlank
    @Schema(description = "Student class for all questions", example = "10th Grade")
    private String studentClass;

    @NotEmpty
    @Schema(description = "List of questions to create")
    private List<SingleQuestionDTO> questions;

}
package com.spring.jwt.Exam.Dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO for negative marks assigned to specific questions")
public class NegativeMarksDTO {

    @NotNull(message = "Question ID is required")
    @Schema(description = "ID of the question", example = "101", required = true)
    private Integer questionId;

    @NotNull(message = "Negative mark is required")
    @Schema(description = "Negative mark for the question", example = "0.25", required = true)
    private Double negativeMark;
}

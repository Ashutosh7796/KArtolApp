package com.spring.jwt.PaperPattern;

import com.spring.jwt.entity.enum01.QType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data@AllArgsConstructor
@NoArgsConstructor
@Builder@Schema(description = "PaperPattern data transfer object")
public class PaperPatternDto {
    @Schema(description = "unique id of paperPattern", example = "1")
    private Integer paperPatternId;
    @NotBlank(message = "subject name is required")
    @Schema(description = "name of subject", example = "Chemistry",required = true)
    private String subject;
    @Schema(description = "type of question",example = "MCQ")
    private QType type;
    @Schema(description = "name of pattern")
    private String patternName;
    @Schema(description = "number of questions")
    private Integer noOfQuestion;
    @Schema(description = "questions required")
    private Integer requiredQuestion;
    @Schema(description = "negative marking")
    private Integer negativeMarks;
    @Schema(description = "Total marks of pattern")
    private Integer marks;

    private Integer MCQ;

    private Integer DESCRIPTIVE;
}
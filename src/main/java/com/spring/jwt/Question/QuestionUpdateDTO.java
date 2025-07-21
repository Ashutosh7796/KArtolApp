package com.spring.jwt.Question;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.spring.jwt.entity.enum01.QType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Data Transfer Object for updating questions (partial update)")
public class QuestionUpdateDTO {

    private String questionText;
    private QType type;
    private String subject;
    private String unit;
    private String chapter;
    private String topic;
    private String level;
    private Integer marks;
    private Integer userId;
    private String option1;
    private String option2;
    private String option3;
    private String option4;
    private String answer;

    @JsonProperty("studentClass")
    private String StudentClass;

    @JsonProperty("descriptive")
    private Boolean isDescriptive; // Use boxed Boolean to allow null for "not set"

    @JsonProperty("MultiOptions")
    private Boolean isMultiOptions; // Use boxed Boolean

    private String hintAndSol;
}

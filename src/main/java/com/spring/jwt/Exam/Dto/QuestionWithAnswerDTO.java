package com.spring.jwt.Exam.Dto;

import com.spring.jwt.entity.enum01.QType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QuestionWithAnswerDTO {
    private Integer questionId;
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

    private String studentClass;

    private boolean isDescriptive;
    private boolean isMultiOptions;

    private String answer;
    private String hintAndSol;
}

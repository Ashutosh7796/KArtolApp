package com.spring.jwt.Exam.Dto;

import lombok.Data;

@Data
public class QuestionNoAnswerDTO {
    private Integer questionId;
    private String questionText;
    private String type;
    private String subject;
    private String level;
    private String marks;
    private Integer userId;
    private String option1;
    private String option2;
    private String option3;
    private String option4;
    private String studentClass;
    // NO answer field!
}
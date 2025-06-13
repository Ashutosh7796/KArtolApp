package com.spring.jwt.Exam.Dto;

import lombok.Data;

@Data
public class UserAnswerDTO {
    private Integer id;
    private Integer sessionId;
    private Integer questionId;
    private String selectedOption;
}
package com.spring.jwt.Exam.Dto;

import lombok.Data;

@Data
public class PaperQuestionDTO {
    private Integer id;
    private Integer paperId;
    private Integer questionId;
    private Integer questionOrder;
    private String StudentClass;
}
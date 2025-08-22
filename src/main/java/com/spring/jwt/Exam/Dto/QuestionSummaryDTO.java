package com.spring.jwt.Exam.Dto;

import lombok.Data;

@Data
public class QuestionSummaryDTO {
    private Integer questionId;
    private Integer marks;
    private boolean isDescriptive;

    public QuestionSummaryDTO(Integer questionId, Integer marks, boolean isDescriptive) {
        this.questionId = questionId;
        this.marks = marks;
        this.isDescriptive = isDescriptive;
    }
}

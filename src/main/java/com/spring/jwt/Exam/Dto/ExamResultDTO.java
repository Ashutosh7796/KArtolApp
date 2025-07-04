package com.spring.jwt.Exam.Dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ExamResultDTO {
    private Integer sessionId;
    private Integer paperId;
    private String paperTitle;
    private Double score;
    private String studentClass;
    private String status;
    private Double negativeCount;
    private Double negativeScore;
    private LocalDateTime resultDate;
}
package com.spring.jwt.Exam.Dto;

import lombok.Data;

@Data
public class ExamResultDTO {
    private Integer sessionId;
    private Integer paperId;
    private String paperTitle;
    private Integer score;
    private String studentClass;
    private String status; // e.g., "Submitted" or "In Progress"
}
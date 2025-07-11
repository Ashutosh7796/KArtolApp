package com.spring.jwt.Exam.Dto;

import java.time.LocalDateTime;

public class ExamPaperSummaryDto {
    private Integer paperId;
    private String paperName;
    private LocalDateTime startTime;
    private LocalDateTime resultDate;

    public ExamPaperSummaryDto(Integer paperId, String paperName, LocalDateTime startTime, LocalDateTime resultDate) {
        this.paperId = paperId;
        this.paperName = paperName;
        this.startTime = startTime;
        this.resultDate = resultDate;
    }

    // Getters and setters
}

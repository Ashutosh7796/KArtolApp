package com.spring.jwt.Exam.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamPaperSummaryDto {
    private Integer paperId;
    private String title;
    private LocalDateTime startTime;
    private LocalDateTime resultDate;



    // Getters and setters
}

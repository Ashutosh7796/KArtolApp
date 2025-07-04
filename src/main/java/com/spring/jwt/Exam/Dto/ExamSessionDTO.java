package com.spring.jwt.Exam.Dto;


import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ExamSessionDTO {
    private Integer sessionId;
    private Integer userId;
    private Integer paperId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime resultDate;
    private Double score;
    private String StudentClass;
    private Double negativeCount;
    private Double negativeScore;
    private List<UserAnswerDTO> userAnswers;
}

package com.spring.jwt.Exam.Dto;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamSessionShowResultDto {
    private Integer sessionId;
    private Integer userId;
    private String studentName;
    private Integer paperId;
    private String title;
    private String studentClass;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime resultDate;
    private Double score;
    private Double negativeCount;
    private Double negativeScore;
}

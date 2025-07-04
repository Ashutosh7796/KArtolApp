package com.spring.jwt.Exam.Dto;

import com.spring.jwt.Exam.entity.ExamResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamResultDTO {
    private Integer resultId;
    private Integer userId;
    private Integer paperId;
    private String paperTitle;
    private String studentClass;
    private LocalDateTime examStartTime;
    private LocalDateTime examEndTime;
    private LocalDateTime resultProcessedTime;
    private Double score;
    private Double negativeCount;
    private Double negativeScore;
    private Integer totalQuestions;
    private Integer correctAnswers;
    private Integer incorrectAnswers;
    private Integer unansweredQuestions;
    private Integer originalSessionId;
    
    // Fields for backward compatibility
    private Integer sessionId;
    private String status;
    
    /**
     * Convert ExamResult entity to DTO
     * @param result The exam result entity
     * @return ExamResultDTO
     */
    public static ExamResultDTO fromEntity(ExamResult result) {
        if (result == null) {
            return null;
        }
        
        ExamResultDTO dto = new ExamResultDTO();
        dto.setResultId(result.getResultId());
        dto.setUserId(result.getUser().getId().intValue());
        dto.setPaperId(result.getPaper().getPaperId());
        dto.setPaperTitle(result.getPaper().getTitle());
        dto.setStudentClass(result.getStudentClass());
        dto.setExamStartTime(result.getExamStartTime());
        dto.setExamEndTime(result.getExamEndTime());
        dto.setResultProcessedTime(result.getResultProcessedTime());
        dto.setScore(result.getScore());
        dto.setNegativeCount(result.getNegativeCount());
        dto.setNegativeScore(result.getNegativeScore());
        dto.setTotalQuestions(result.getTotalQuestions());
        dto.setCorrectAnswers(result.getCorrectAnswers());
        dto.setIncorrectAnswers(result.getIncorrectAnswers());
        dto.setUnansweredQuestions(result.getUnansweredQuestions());
        dto.setOriginalSessionId(result.getOriginalSessionId());
        
        // Set backward compatibility fields
        dto.setSessionId(result.getResultId());
        dto.setStatus("Completed");
        
        return dto;
    }
}
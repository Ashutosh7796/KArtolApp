package com.spring.jwt.Exam.Dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
@Data
public class PaperWithQuestionsDTOn {
    private Integer sessionId; // <-- Add this line
    private Integer paperId;
    private String title;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Boolean isLive;
    private String studentClass;
    private List<QuestionNoAnswerDTO> questions;
}

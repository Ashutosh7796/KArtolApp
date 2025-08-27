package com.spring.jwt.Exam.Dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
@Data
public class PaperWithQuestionsDTOnn {
    private Integer sessionId;
    private Integer paperId;
    private String title;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Boolean isLive;
    private String studentClass;
    private Integer paperPatternId;
    private LocalDateTime paperEndTime;
    private String duration;
    private List<QuestionNoAnswerDTO> questions;
}

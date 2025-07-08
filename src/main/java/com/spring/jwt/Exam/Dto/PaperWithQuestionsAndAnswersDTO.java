package com.spring.jwt.Exam.Dto;

import com.spring.jwt.entity.Question;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaperWithQuestionsAndAnswersDTO {

    private Integer paperId;
    private String title;
    private String description;
    private String studentClass;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Boolean isLive;
    private Integer paperPatternId;

    private List<QuestionWithAnswerDTO> questions;  // You can change this to a custom QuestionDTO list if needed
}

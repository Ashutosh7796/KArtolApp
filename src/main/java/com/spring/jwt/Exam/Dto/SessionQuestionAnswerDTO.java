package com.spring.jwt.Exam.Dto;

import lombok.Data;

@Data
public class SessionQuestionAnswerDTO {
    private Long questionId;
    private String questionText;
    private String[] options; // or List<String>
    private String submittedAnswer;
    private String correctAnswer; // optional, only if you want to show it
}
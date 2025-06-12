package com.spring.jwt.Question;


import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDTO {
    private Integer questionId;
    private String questionText;
    private String type;
    private String subject;
    private String level;
    private String marks;
    private String option1;
    private String option2;
    private String option3;
    private String option4;
    private String answer;
}

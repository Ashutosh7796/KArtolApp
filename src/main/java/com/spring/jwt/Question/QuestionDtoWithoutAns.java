package com.spring.jwt.Question;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDtoWithoutAns {
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
}

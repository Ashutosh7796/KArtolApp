package com.spring.jwt.Question;

import com.spring.jwt.entity.enum01.QType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDtoWithoutAns {
    private Integer questionId;
    private String questionText;
    private QType type;
    private String subject;
    private String level;
    private Integer marks;
    private String option1;
    private String option2;
    private String option3;
    private String option4;
    private boolean isDescriptive;
}

package com.spring.jwt.Question;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDto {
    private Integer questionId;
    private String question;
    private String op1;
    private String op2;
    private String op3;
    private String op4;
    private String ans;
    private String type;
    private String sub;
    private String level;
    private String marks;
    private String questioncol;
    private Integer userId;
}

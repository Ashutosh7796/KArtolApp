package com.spring.jwt.Exam.Dto;

import com.spring.jwt.entity.enum01.QType;
import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import lombok.Data;

@Data
public class QuestionDTO {
    private Integer questionId;
    private String questionText;
    private QType type;
    private String subject;
    private String level;
    private Integer marks;
    private Integer userId;
    private String option1;
    private String option2;
    private String option3;
    private String option4;
    private String StudentClass;
    // Usually, do NOT expose the answer in DTO for exam-takers!
    // Include if needed for admin or after exam is over.
    private String answer;
    private String hintAndSol;
    private boolean isDescriptive;
}
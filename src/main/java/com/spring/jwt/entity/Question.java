package com.spring.jwt.entity;

import com.spring.jwt.Exam.entity.Paper;
import com.spring.jwt.entity.enum01.QType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import jakarta.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer questionId;

    private String questionText;
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private QType type;
    private String subject;
    private String level;
    private String marks;
    private Integer userId;

    // Four fixed options as separate columns
    private String option1;
    private String option2;
    private String option3;
    private String option4;
    private String StudentClass;
    private String hintAndSol;
    private boolean isDescriptive;


    // Store the answer, e.g. "option1", "option2", etc. or the text itself
    private String answer;
    @ManyToOne
    @JoinColumn(name = "paper_id")
    private Paper paper;
}

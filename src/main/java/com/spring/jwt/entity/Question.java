package com.spring.jwt.entity;

import com.spring.jwt.Exam.entity.Paper;
import com.spring.jwt.entity.enum01.QType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import jakarta.persistence.*;
import org.hibernate.annotations.BatchSize;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer questionId;
    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String questionText;
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private QType type;
    private String subject;
    private String unit;
    private String chapter;
    private String topic;
    private String level;
    private Integer marks;
    private Integer userId;

    // Four fixed options as separate columns
    private String option1;
    private String option2;
    private String option3;
    private String option4;
    private String StudentClass;
    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String hintAndSol;
    @NotNull
    private boolean isDescriptive;
    @NotNull
    private boolean isMultiOptions;
    @NotNull
    private boolean deleted;


    // Store the answer, e.g. "option1", "option2", etc. or the text itself
    private String answer;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paper_id")
    private Paper paper;


}

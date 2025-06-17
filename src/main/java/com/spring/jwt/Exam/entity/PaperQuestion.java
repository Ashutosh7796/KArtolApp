package com.spring.jwt.Exam.entity;


import com.spring.jwt.entity.Question;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaperQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "paper_id")
    private Paper paper;
    private String StudentClass;
    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    private Integer questionOrder; // Order of this question in the paper
}
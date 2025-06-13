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
public class UserAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "session_id")
    private ExamSession examSession;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    private String selectedOption; // e.g., "option1", "option2", or the actual text
}
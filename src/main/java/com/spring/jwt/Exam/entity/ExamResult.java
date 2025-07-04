package com.spring.jwt.Exam.entity;

import com.spring.jwt.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity to store finalized exam results after the result date has passed
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "exam_results")
public class ExamResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer resultId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "paper_id")
    private Paper paper;
    
    private String studentClass;
    private LocalDateTime examStartTime;
    private LocalDateTime examEndTime;
    private LocalDateTime resultProcessedTime;
    private Double score;
    private Double negativeCount;
    private Double negativeScore;

    private Integer totalQuestions;
    private Integer correctAnswers;
    private Integer incorrectAnswers;
    private Integer unansweredQuestions;

    private Integer originalSessionId;
} 
package com.spring.jwt.Exam.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NegativeMarks {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long negativeMarksId;

    private Integer questionId;

    private double negativeMark;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paper_id", nullable = false)
    private Paper paper;
}

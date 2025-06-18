package com.spring.jwt.entity;

import lombok.*;
import jakarta.persistence.*;

@Entity
@Table(name = "assessment_questions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssessmentQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "assessment_id")
    private Assessment assessment;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;
    
    @Column(name = "points", nullable = false)
    private Integer points;
    
    @Column(name = "order_number", nullable = false)
    private Integer orderNumber;
}
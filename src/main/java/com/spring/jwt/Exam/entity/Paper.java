package com.spring.jwt.Exam.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Paper {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer paperId;

    private String title;
    private String description;
    private String StudentClass;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Boolean isLive;

    @OneToMany(mappedBy = "paper", cascade = CascadeType.ALL)
    private List<PaperQuestion> paperQuestions;
}
package com.spring.jwt.Exam.entity;


import com.spring.jwt.entity.PaperPattern;
import com.spring.jwt.entity.Question;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

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
    private String studentClass;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime paperEndTime;
    private Boolean isLive;
    private LocalDateTime resultDate;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "paper_pattern_id")
    private PaperPattern paperPattern;


    @OneToMany(mappedBy = "paper", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.LAZY)
    private List<PaperQuestion> paperQuestions;


    @OneToMany(mappedBy = "paper", fetch = FetchType.LAZY) // or EAGER as needed
    private List<Question> questions;

    @OneToMany(mappedBy = "paper", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NegativeMarks> negativeMarksList;


    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }
}
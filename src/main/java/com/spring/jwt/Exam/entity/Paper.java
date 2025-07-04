package com.spring.jwt.Exam.entity;


import com.spring.jwt.entity.PaperPattern;
import com.spring.jwt.entity.Question;
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
    private String studentClass;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Boolean isLive;
    private LocalDateTime resultDate;
    @ManyToOne(fetch = FetchType.LAZY)

    @JoinColumn(name = "paper_pattern_id", insertable = false, updatable = false)
    private PaperPattern paperPattern;




    @OneToMany(mappedBy = "paper", cascade = CascadeType.ALL)
    private List<PaperQuestion> paperQuestions;

    @OneToMany(mappedBy = "paper", fetch = FetchType.LAZY) // or EAGER as needed
    private List<Question> questions;

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }
}
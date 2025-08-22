package com.spring.jwt.entity;

import com.spring.jwt.entity.enum01.QType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "paperPattern")
public class PaperPattern {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer paperPatternId;
    private String subject;
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private QType type;
    private String patternName;
    private Integer noOfQuestion;
    private Integer requiredQuestion;
    private Integer negativeMarks;
    private Integer marks;
    private Integer MCQ;
    private Integer DESCRIPTIVE;

    
}

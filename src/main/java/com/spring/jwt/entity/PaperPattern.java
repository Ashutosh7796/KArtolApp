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
    @Column(name = "subject")
    private String subject;
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private QType type;
    @Column(name = "paper_Pattern")
    private String patternName;
    @Column(name = "no_Of_Question")
    private Integer noOfQuestion;
    @Column(name = "required_Question")
    private Integer requiredQuestion;
    @Column(name = "negative_Marks")
    private Integer negativeMarks;
    @Column(name = "marks")
    private Integer marks;
    
}

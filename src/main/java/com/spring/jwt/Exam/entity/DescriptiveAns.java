package com.spring.jwt.Exam.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DescriptiveAns {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer descriptiveAnsId;
    @Lob
    private String ans;
    private Integer userId;
    private Integer questionId;
    private Integer paperId;
}

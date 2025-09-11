package com.spring.jwt.Exam.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for a student's score in a single subject
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubjectScoreDto {
    private String subjectName;
    private double score;
}

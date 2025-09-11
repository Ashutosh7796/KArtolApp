package com.spring.jwt.Exam.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for each student's result
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentResultDto {
    private String studentName;
    private List<SubjectScoreDto> subjects; // ✅ Each student has subjects with scores
    private double averageScore; // ✅ Average score across all subjects for this student
    private int rank;
}

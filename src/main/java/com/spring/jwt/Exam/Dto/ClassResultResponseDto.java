package com.spring.jwt.Exam.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Final response DTO for entire class result
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassResultResponseDto {
    private String studentClass;
    private List<StudentResultDto> studentResults; // ✅ Changed to student-wise
    private Double overallAverageScore;
}

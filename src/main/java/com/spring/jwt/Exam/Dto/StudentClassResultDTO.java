package com.spring.jwt.Exam.Dto;

import lombok.Data;
import java.util.List;

@Data
public class StudentClassResultDTO {
    private String studentClass;
    private List<ExamResultDTO> results;
}
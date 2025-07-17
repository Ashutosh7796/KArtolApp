package com.spring.jwt.StudentAttendance;

import lombok.Data;

import java.util.List;

@Data
public class StudentAttendanceSummaryResponseDto {
    private Integer userId;
    private String name;
    private String studentClass;
    private List<SubjectAttendanceSummaryDto> subjectWiseSummary;
}

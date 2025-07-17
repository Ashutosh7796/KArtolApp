package com.spring.jwt.StudentAttendance;

import lombok.Data;

@Data
public class SubjectAttendanceSummaryDto {
    private String subject;
    private long presentCount;
    private long absentCount;
    private long totalCount;
    private double percentage;
}

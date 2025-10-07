package com.spring.jwt.StudentAttendance;

import lombok.Data;

@Data
public class SubjectAttendanceDTO {
    private String sub;
    private Double attendancePercentage;
    private Long totalPresent;
}

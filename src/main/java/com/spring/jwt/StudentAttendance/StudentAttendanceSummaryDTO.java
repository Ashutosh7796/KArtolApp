package com.spring.jwt.StudentAttendance;

import lombok.Data;
import java.util.List;

@Data
public class StudentAttendanceSummaryDTO {
    private Integer userId;
    private String name;
    private String studentClass;
    private List<SubjectAttendanceDTO> subjects;
    private Double overallPercentage; // average of all subject percentages
}

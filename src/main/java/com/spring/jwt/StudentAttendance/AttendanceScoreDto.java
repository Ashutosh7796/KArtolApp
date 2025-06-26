package com.spring.jwt.StudentAttendance;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceScoreDto {
    private Integer totalAttendancePercentage;
    private Integer weeklyScorePercentage;
    private Integer monthlyScorePercentage;
    private Integer yearlyScorePercentage;
}

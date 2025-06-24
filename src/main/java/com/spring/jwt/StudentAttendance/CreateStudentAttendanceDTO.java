package com.spring.jwt.StudentAttendance;


import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class CreateStudentAttendanceDTO {
    private Date date;
    private String sub;
    private String name;
    private String mark;
    private Integer teacherId;
    private String studentClass;
    private List<SingleAttendanceDTO> attendanceList;
}
package com.spring.jwt.StudentAttendance;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Date;

@Data
public class StudentAttendanceDTO {
    private Integer studentAttendanceId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDate date;

    @NotBlank(message = "Subject is mandatory")
    private String sub;

    @NotBlank(message = "Name is mandatory")
    private String name;

    @NotBlank(message = "Mark is mandatory")
    private String mark;

    @NotNull(message = "Attendance is mandatory")
    private Boolean attendance;

    @NotNull(message = "User ID is mandatory")
    private Integer userId;

    @NotNull(message = "Teacher ID is mandatory")
    private Integer teacherId;

    @NotBlank(message = "Student class is mandatory")
    private String studentClass;
}
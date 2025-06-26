package com.spring.jwt.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "studentAttendance")
public class StudentAttendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer studentAttendanceId;
//    @NotNull(message = "Date is required")
    private LocalDate date;
    private String sub;
    private String name;
    private String mark;
    private Boolean attendance;
    private Integer userId;
    private Integer teacherId;
    private String studentClass;
}

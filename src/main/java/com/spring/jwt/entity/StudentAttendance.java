package com.spring.jwt.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "studentAttendance")
public class StudentAttendance {

    @Id
    private Integer studentAttendanceId;

    private Date date;
    private String sub;
    private String name;
    private String mark;
    private Boolean attendance;
    private Integer userId;
    private Integer teacherId;
    private String studentClass;
}

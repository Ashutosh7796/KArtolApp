package com.spring.jwt.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "teachersAttendance")
public class TeachersAttendance {

    @Id
    private Integer teachersAttendanceId;

    private String name;
    private String month;
    private String date;
    private String inTime;
    private String outTime;
    private String mark;
    private Integer teacherId;
}

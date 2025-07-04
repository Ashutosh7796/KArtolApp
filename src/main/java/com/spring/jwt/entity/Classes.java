package com.spring.jwt.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Classes")
public class Classes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long classesId;
    @Column(name = "sub")
    private String sub;
    @Column(name = "date")
    private LocalDate date;
    @Column(name = "duration")
    private String duration;
    @Column(name = "student_Class")
    private String studentClass;
    @Column(name = "teacher_Id")
    private Integer teacherId;}
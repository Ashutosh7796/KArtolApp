package com.spring.jwt.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "assessment")
public class Assessment {

    @Id
    private Integer assessmentId;
    private Integer questionId;
    private String question;
    private String op1;
    private String op2;
    private String op3;
    private String op4;
    private String ans;
    private String type;
    private String sub;
    private String level;
    private String marks;
    private String quastioncol;
    private String assessmentDate;
    private String time;
    private String duration;
    private String teacherName;
}

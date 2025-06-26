package com.spring.jwt.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "notes")
public class Notes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long NotesId;
    private String StudentClass;
    private String sub;
    private String chapter;
    private String topic;
    private String Note1;
    private String Note2;
    private Integer teacherId;
    private LocalDate createdDate;
}

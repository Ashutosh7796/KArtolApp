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
    private Long notesId;

    private String studentClass;
    private String sub;
    private String chapter;
    private String topic;
    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String note1;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String note2;
    private Integer teacherId;
    private LocalDate createdDate;
}

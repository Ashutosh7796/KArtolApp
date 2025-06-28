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
@Table(name = "notes")
public class Notes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long notesId;
    @Column(name = "standard")
    private String standard;
    @Column(name = "sub ")
    private String sub;
    @Column(name = "chapter")
    private String chapter;
    @Column(name = "topic")
    private String topic;
    @Column(name = "Note1")
    private String Note1;
    @Column(name = "Note2")
    private String Note2;
    @Column(name = "teacher_Id")
    private Integer teacherId;
    @Column(name= "created_Date")
    private Date createdDate;
}
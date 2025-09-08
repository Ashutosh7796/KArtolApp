package com.spring.jwt.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "fees")
public class Fees {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer feesId;
    private String name;
    private Integer fee;
    private String type;
    private String studentClass;
    private  String status;
    private Date date;
    private String batch;


}

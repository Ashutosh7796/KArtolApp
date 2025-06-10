package com.spring.jwt.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "fees")
public class Fees {

    @Id
    private Integer feesId;
    private String name;
    private Integer fee;
    private String type;
    private String StudentClass;
}

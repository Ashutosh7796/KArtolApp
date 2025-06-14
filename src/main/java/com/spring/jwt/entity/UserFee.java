package com.spring.jwt.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "userFee")
public class UserFee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userFeeId;

    private String date;
    private String amount;
    private String totalFees;
    private String remainingFees;
    private String paidFeesOnDate;
    private String type;
    private Integer feesId;
    private Integer userId;

}

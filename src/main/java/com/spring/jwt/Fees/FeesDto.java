package com.spring.jwt.Fees;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeesDto {
    private Integer feesId;
    private String name;
    private Integer fee;
    private String type;
    private String studentClass;
    private String status;
    private Date date;
    private String batch;


}
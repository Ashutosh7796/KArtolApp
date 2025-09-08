package com.spring.jwt.UserFee;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserFeeDTO {
    private Integer userFeeId;
    private String date;
    private String amount;
    private String totalFees;
    private String remainingFees;
    private String paidFeesOnDate;
    private String type;
    private Integer feesId;
    private String status;
    private long userId;
    private String studentClass;
    private String batch;

    // Getters and setters
}

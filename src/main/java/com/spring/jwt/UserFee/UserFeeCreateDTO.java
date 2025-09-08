package com.spring.jwt.UserFee;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserFeeCreateDTO {
    private String amount;
    private String type;
    private long  userId;
}

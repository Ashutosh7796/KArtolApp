package com.spring.jwt.UserFee;

import lombok.Data;

@Data
public class UserFeeResponseDTO {
    private boolean success;
    private String message;
    private Object data;

    public UserFeeResponseDTO(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

}

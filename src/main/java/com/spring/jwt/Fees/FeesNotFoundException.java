package com.spring.jwt.Fees;

public class FeesNotFoundException extends RuntimeException {
    public FeesNotFoundException(String message) {
        super(message);
    }
}
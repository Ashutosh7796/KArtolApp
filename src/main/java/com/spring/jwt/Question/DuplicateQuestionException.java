package com.spring.jwt.Question;

public class DuplicateQuestionException extends RuntimeException {
    public DuplicateQuestionException(String msg) {
        super(msg);
    }
}
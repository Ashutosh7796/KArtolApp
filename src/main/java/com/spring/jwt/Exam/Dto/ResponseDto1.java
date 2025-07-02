package com.spring.jwt.Exam.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDto1<T> {
    private String message;
    private Integer paperId;
    private LocalDate startTime;
    private LocalDate endTime;
    private String exception;

    public static <T> ResponseDto1<T> success(String message, Integer paperId, LocalDate startTime, LocalDate endTime) {
        return new ResponseDto1<>(message, paperId, startTime, endTime, null);
    }

    public static <T> ResponseDto1<T> error(String message, String exception) {
        return new ResponseDto1<>(message, null, null, null, exception);
    }
}

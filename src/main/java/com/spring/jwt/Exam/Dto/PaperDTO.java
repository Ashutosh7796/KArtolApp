package com.spring.jwt.Exam.Dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PaperDTO {
    private Integer paperId;
    private String title;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Boolean isLive;
    private String studentClass;
    private List<Integer> questions; // Just question IDs
}
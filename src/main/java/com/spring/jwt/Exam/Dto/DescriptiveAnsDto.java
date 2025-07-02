package com.spring.jwt.Exam.Dto;

import lombok.Data;

@Data
public class DescriptiveAnsDto {


    private Integer descriptiveAnsId;
    private String ans;
    private Integer userId;
    private Integer questionId;
    private Integer paperId;
}


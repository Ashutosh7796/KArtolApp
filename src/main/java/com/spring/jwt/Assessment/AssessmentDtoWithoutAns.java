package com.spring.jwt.Assessment;

import com.spring.jwt.Question.QuestionDtoWithoutAns;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentDtoWithoutAns {
    private Integer assessmentId;
    private Long setNumber;
    private String assessmentDate;
    private String duration;
    private String startTime;
    private String endTime;
    private Integer userId;
    private List<Integer> questionIds;
    private List<QuestionDtoWithoutAns> questions;
}

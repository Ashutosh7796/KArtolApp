package com.spring.jwt.Assessment;


import com.spring.jwt.Question.QuestionDTO;
import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentDTO {
    private Integer assessmentId;
    private Long setNumber;
    private String assessmentDate;
    private String duration;
    private String startTime;
    private String endTime;
    private Integer userId;
    private List<Integer> questionIds;
    private List<QuestionDTO> questions;

}
package com.spring.jwt.Assessment;

import java.util.List;

public interface AssessmentService {
    List<AssessmentDTO> createAssessmentsBulk(List<AssessmentDTO> dtos);
    AssessmentDTO getAssessmentById(Integer id);
    List<AssessmentDTO> getAllAssessments();
    AssessmentDTO updateAssessment(Integer id, AssessmentDTO dto);
    void deleteAssessment(Integer id);
}
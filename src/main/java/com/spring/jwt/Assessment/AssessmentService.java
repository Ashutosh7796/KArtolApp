package com.spring.jwt.Assessment;

import java.util.List;

public interface AssessmentService {
    public List<AssessmentDTO> createAssessmentsBulk(AssessmentDTO dto);
    AssessmentDTO getAssessmentById(Integer id);
    public List<AssessmentDtoWithoutAns> getAllAssessments();
    AssessmentDTO updateAssessment(Integer id, AssessmentDTO dto);
    void deleteAssessment(Integer id);

    public AssessmentDtoWithoutAns getAssessmentByIdWithoutAns(Integer id);
}
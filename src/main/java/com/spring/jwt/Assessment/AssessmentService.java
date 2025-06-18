package com.spring.jwt.Assessment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * Service interface for managing Assessment operations
 */
public interface AssessmentService {
    /**
     * Create a new assessment
     * @param assessmentDTO the assessment data to create
     * @return the created assessment DTO
     */
    AssessmentDTO createAssessment(AssessmentDTO assessmentDTO);
    
    /**
     * Get an assessment by its ID
     * @param id the assessment ID
     * @return the assessment DTO
     * @throws AssessmentNotFoundException if assessment not found
     */
    AssessmentDTO getAssessmentById(Integer id);
    
    /**
     * Get all assessments with pagination and sorting
     * @param pageable pagination and sorting parameters
     * @return a page of assessment DTOs
     */
    Page<AssessmentDTO> getAllAssessments(Pageable pageable);
    
    /**
     * Get all assessments without pagination
     * @return list of all assessment DTOs
     */
    List<AssessmentDTO> getAllAssessments();
    
    /**
     * Update an existing assessment
     * @param id the assessment ID to update
     * @param assessmentDTO the assessment data to update
     * @return the updated assessment DTO
     * @throws AssessmentNotFoundException if assessment not found
     */
    AssessmentDTO updateAssessment(Integer id, AssessmentDTO assessmentDTO);
    
    /**
     * Delete an assessment by its ID
     * @param id the assessment ID to delete
     * @throws AssessmentNotFoundException if assessment not found
     */
    void deleteAssessment(Integer id);
    
    /**
     * Get assessments by user ID (creator)
     * @param userId the user ID
     * @param pageable pagination and sorting parameters
     * @return a page of assessment DTOs
     */
    Page<AssessmentDTO> getAssessmentsByUserId(Integer userId, Pageable pageable);
    
    /**
     * Get assessments by user ID without pagination
     * @param userId the user ID
     * @return list of assessment DTOs
     */
    List<AssessmentDTO> getAssessmentsByUserId(Integer userId);
    
    /**
     * Search assessments with dynamic filtering
     * @param filters map of field names and values to filter by
     * @param pageable pagination and sorting parameters
     * @return a page of assessment DTOs matching the filters
     */
    Page<AssessmentDTO> searchAssessments(Map<String, String> filters, Pageable pageable);
    
    /**
     * Add a question to an assessment
     * @param assessmentId the assessment ID
     * @param questionDTO the question data to add
     * @return the updated assessment DTO
     * @throws AssessmentNotFoundException if assessment not found
     * @throws DuplicateQuestionInSetException if question already exists in the assessment
     */
    AssessmentDTO addQuestionToAssessment(Integer assessmentId, AssessmentDTO.AssessmentQuestionDTO questionDTO);
    
    /**
     * Remove a question from an assessment
     * @param assessmentId the assessment ID
     * @param questionId the question ID to remove
     * @return the updated assessment DTO
     * @throws AssessmentNotFoundException if assessment not found
     */
    AssessmentDTO removeQuestionFromAssessment(Integer assessmentId, Integer questionId);
}
package com.spring.jwt.Exam.service;

import com.spring.jwt.Exam.Dto.ExamResultDTO;
import com.spring.jwt.Exam.entity.ExamResult;
import com.spring.jwt.Exam.entity.ExamSession;

import java.util.List;

/**
 * Service interface for managing exam results
 */
public interface ExamResultService {
    
    /**
     * Process exam sessions that have reached their result date
     * This method will be called by a scheduler
     * @return Number of sessions processed
     */
    int processReadyExamResults();
    
    /**
     * Process a specific exam session and move it to results
     * @param session The exam session to process
     * @return The created exam result
     */
    ExamResult processExamSession(ExamSession session);
    
    /**
     * Get all exam results for a specific user
     * @param userId The user ID
     * @return List of exam result DTOs
     */
    List<ExamResultDTO> getResultsByUserId(Long userId);
    
    /**
     * Get all exam results for a specific paper
     * @param paperId The paper ID
     * @return List of exam result DTOs
     */
    List<ExamResultDTO> getResultsByPaperId(Integer paperId);
    
    /**
     * Get all exam results for a specific student class
     * @param studentClass The student class
     * @return List of exam result DTOs
     */
    List<ExamResultDTO> getResultsByStudentClass(String studentClass);
    
    /**
     * Fix the result date format in the database
     * @return Number of records updated
     */
    int fixResultDateFormat();
} 
package com.spring.jwt.Question;

import com.spring.jwt.entity.enum01.QType;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * Service interface for managing Question operations
 */
public interface QuestionService {
    /**
     * Create a new question
     * @param questionDTO the question data to create
     * @return the created question DTO
     */
    QuestionDTO createQuestion(@Valid QuestionDTO questionDTO);
    
    /**
     * Get a question by its ID
     * @param id the question ID
     * @return the question DTO
     * @throws QuestionNotFoundException if question not found
     */
    QuestionDTO getQuestionById(Integer id);
    
    /**
     * Get all questions with pagination and sorting
     * @param pageable pagination and sorting parameters
     * @return a page of question DTOs
     */
    Page<QuestionDTO> getAllQuestions(Pageable pageable);
    
    /**
     * Get all questions without pagination
     * @return list of all question DTOs
     */
    List<QuestionDTO> getAllQuestions();
    
    /**
     * Update an existing question
     * @param id the question ID to update
     *  the question data to update
     * @return the updated question DTO
     * @throws QuestionNotFoundException if question not found
     */
    QuestionDTO updateQuestion(Integer id, QuestionUpdateDTO questionUpdateDTO);
    
    /**
     * Delete a question by its ID
     * @param id the question ID to delete
     * @throws QuestionNotFoundException if question not found
     */
    void deleteQuestion(Integer id);
    
    /**
     * Get questions by user ID
     * @param userId the user ID
     * @param pageable pagination and sorting parameters
     * @return a page of question DTOs
     */
    Page<QuestionDTO> getQuestionsByUserId(Integer userId, Pageable pageable);
    
    /**
     * Get questions by user ID without pagination
     * @param userId the user ID
     * @return list of question DTOs
     */
    List<QuestionDTO> getQuestionsByUserId(Integer userId);
    
    /**
     * Search questions with dynamic filtering
     * @param filters map of field names and values to filter by
     * @param pageable pagination and sorting parameters
     * @return a page of question DTOs matching the filters
     */
    Page<QuestionDTO> searchQuestions(Map<String, String> filters, Pageable pageable);
    
    /**
     * Get questions filtered by subject, type, level, and marks
     * @param subject the subject to filter by (optional)
     * @param type the type to filter by (optional)
     * @param level the level to filter by (optional)
     * @param marks the marks to filter by (optional)
     * @return list of question DTOs matching the criteria
     */
    List<QuestionDTO> getQuestionsBySubTypeLevelMarks(String subject, QType type, String level, String marks);

    public List<QuestionDTO> createQuestionsBulk(@Valid BulkQuestionDTO bulkDTO);

    public Page<QuestionDTO> getAllQuestionsDeleted(Pageable pageable);

    void updateDeletedStatus(Integer questionId, boolean deleted);
    void toggleDeletedStatus(Integer questionId);

    Page<QuestionDTO> getQuestionsByUserIdAndDescriptive(Integer userId, boolean isDescriptive, Pageable pageable);
}
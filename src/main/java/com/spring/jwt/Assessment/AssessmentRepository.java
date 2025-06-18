package com.spring.jwt.Assessment;

import com.spring.jwt.entity.Assessment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssessmentRepository extends JpaRepository<Assessment, Integer>, JpaSpecificationExecutor<Assessment> {

    /**
     * Returns the maximum setNumber in the Assessment table, or 999 if none found.
     */
    @Query("SELECT COALESCE(MAX(a.setNumber), 999) FROM Assessment a")
    Long findMaxSetNumber();

    /**
     * Find assessments by setNumber and by contained questions.
     * The 'questions' field is a @ManyToMany in Assessment.
     */
    @Query("SELECT a FROM Assessment a JOIN a.questions q WHERE a.setNumber = :setNumber AND q.questionId IN :questionIds")
    List<Assessment> findBySetNumberAndQuestionIds(@Param("setNumber") Long setNumber, @Param("questionIds") List<Integer> questionIds);

    /**
     * Finds assessments by user ID with pagination
     * @param userId the user ID
     * @param pageable pagination parameters
     * @return page of assessments
     */
    @Query("SELECT a FROM Assessment a WHERE a.user.id = :userId")
    Page<Assessment> findByUserId(@Param("userId") Long userId, Pageable pageable);
    
    /**
     * Finds assessments by user ID
     * @param userId the user ID
     * @return list of assessments
     */
    @Query("SELECT a FROM Assessment a WHERE a.user.id = :userId")
    List<Assessment> findByUserId(@Param("userId") Long userId);
    
    /**
     * Finds assessments by subject
     * @param subject the subject
     * @param pageable pagination parameters
     * @return page of assessments
     */
    Page<Assessment> findBySubjectIgnoreCase(String subject, Pageable pageable);
    
    /**
     * Finds assessments containing title (case insensitive)
     * @param title the title substring to search for
     * @param pageable pagination parameters
     * @return page of assessments
     */
    Page<Assessment> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    
    /**
     * Finds assessments by active status
     * @param isActive whether the assessment is active
     * @param pageable pagination parameters
     * @return page of assessments
     */
    Page<Assessment> findByIsActive(Boolean isActive, Pageable pageable);
    
    /**
     * Finds assessments by subject and active status
     * @param subject the subject
     * @param isActive whether the assessment is active
     * @param pageable pagination parameters
     * @return page of assessments
     */
    Page<Assessment> findBySubjectIgnoreCaseAndIsActive(String subject, Boolean isActive, Pageable pageable);
}
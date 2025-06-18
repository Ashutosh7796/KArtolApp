package com.spring.jwt.Question;

import com.spring.jwt.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Question entity with pagination and sorting support
 */
@Repository
public interface QuestionRepository extends JpaRepository<Question, Integer>, JpaSpecificationExecutor<Question> {
    /**
     * Finds all questions by user id with pagination and sorting
     * @param userId the user ID
     * @param pageable pagination and sorting parameters
     * @return page of questions
     */
    Page<Question> findByUserId(Integer userId, Pageable pageable);
    
    /**
     * Finds all questions by user id without pagination
     * @param userId the user ID
     * @return list of questions
     */
    List<Question> findByUserId(Integer userId);

    /**
     * Finds all questions matching the given specification with pagination
     * @param spec the specification to apply
     * @param pageable pagination and sorting parameters
     * @return page of questions
     */
    Page<Question> findAll(Specification<Question> spec, Pageable pageable);
    
    /**
     * Finds all questions matching the given specification without pagination
     * @param spec the specification to apply
     * @return list of questions
     */
    List<Question> findAll(Specification<Question> spec);

    /**
     * Finds a question by its id
     * @param questionId the question ID
     * @return optional question
     */
    Optional<Question> findById(Integer questionId);

    /**
     * Finds questions by optional subject, type, level, and marks with pagination
     * @param subject the subject (optional)
     * @param type the type (optional)
     * @param level the level (optional)
     * @param marks the marks (optional)
     * @param pageable pagination and sorting parameters
     * @return page of questions
     */
    @Query("SELECT q FROM Question q WHERE " +
            "(:subject IS NULL OR q.subject = :subject) AND " +
            "(:type IS NULL OR q.type = :type) AND " +
            "(:level IS NULL OR q.level = :level) AND " +
            "(:marks IS NULL OR q.marks = :marks)")
    Page<Question> findBySubjectTypeLevelMarks(
            @Param("subject") String subject,
            @Param("type") String type,
            @Param("level") String level,
            @Param("marks") String marks,
            Pageable pageable
    );
    
    /**
     * Finds questions by optional subject, type, level, and marks without pagination
     * @param subject the subject (optional)
     * @param type the type (optional)
     * @param level the level (optional)
     * @param marks the marks (optional)
     * @return list of questions
     */
    @Query("SELECT q FROM Question q WHERE " +
            "(:subject IS NULL OR q.subject = :subject) AND " +
            "(:type IS NULL OR q.type = :type) AND " +
            "(:level IS NULL OR q.level = :level) AND " +
            "(:marks IS NULL OR q.marks = :marks)")
    List<Question> findBySubjectTypeLevelMarks(
            @Param("subject") String subject,
            @Param("type") String type,
            @Param("level") String level,
            @Param("marks") String marks
    );

    boolean existsByQuestionText(String questionText);
}
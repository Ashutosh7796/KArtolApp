package com.spring.jwt.Question;

import com.spring.jwt.entity.Question;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Integer> {
    /**
     * Finds all questions by user id.
     */
    List<Question> findByUserId(Integer userId);

    /**
     * Finds all questions matching the given specification (for dynamic queries).
     */
    List<Question> findAll(Specification<Question> spec);

    /**
     * Finds a question by its id.
     */
    Optional<Question> findById(Integer questionId);

    /**
     * Finds questions by optional subject, type, level, and marks.
     * If any parameter is null, it is ignored in the filtering.
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
}
package com.spring.jwt.Assessment;

import com.spring.jwt.entity.Assessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssessmentRepository extends JpaRepository<Assessment, Integer> {

    /**
     * Returns the maximum setNumber in the Assessment table, or 999 if none found.
     */
    @Query("SELECT COALESCE(MAX(a.setNumber), 999) FROM Assessment a")
    Long findMaxSetNumber();

    /**
     * Finds assessments by setNumber and by contained questions.
     * The 'questions' field is a @ManyToMany in Assessment.
     */
    @Query("SELECT a FROM Assessment a JOIN a.questions q WHERE a.setNumber = :setNumber AND q.questionId IN :questionIds")
    List<Assessment> findBySetNumberAndQuestionIds(Long setNumber, List<Integer> questionIds);

}
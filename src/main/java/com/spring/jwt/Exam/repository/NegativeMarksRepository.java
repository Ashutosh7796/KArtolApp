package com.spring.jwt.Exam.repository;

import com.spring.jwt.Exam.entity.NegativeMarks;
import com.spring.jwt.Exam.entity.Paper;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NegativeMarksRepository extends JpaRepository<NegativeMarks, Long> {

    Optional<NegativeMarks> findByPaperAndQuestionId(Paper paper, Integer questionId);
}

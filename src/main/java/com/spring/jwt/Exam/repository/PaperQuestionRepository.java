package com.spring.jwt.Exam.repository;

import com.spring.jwt.Exam.entity.PaperQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaperQuestionRepository extends JpaRepository<PaperQuestion, Integer> {
}
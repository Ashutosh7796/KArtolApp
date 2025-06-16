package com.spring.jwt.Exam.repository;

import com.spring.jwt.Exam.entity.UserAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAnswerRepository extends JpaRepository<UserAnswer, Integer> {
}
package com.spring.jwt.Exam.repository;

import com.spring.jwt.Exam.entity.ExamSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExamSessionRepository extends JpaRepository<ExamSession, Integer> {
    List<ExamSession> findByUser_Id(Long userId);

}
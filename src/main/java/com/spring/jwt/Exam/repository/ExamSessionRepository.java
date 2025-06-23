package com.spring.jwt.Exam.repository;

import com.spring.jwt.Exam.entity.ExamSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExamSessionRepository extends JpaRepository<ExamSession, Integer> {
    List<ExamSession> findByUser_Id(Long userId);

    // Finds the last session for a user, ordered by sessionId descending
    Optional<ExamSession> findTopByUser_IdOrderBySessionIdDesc(Long userId);

}
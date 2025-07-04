package com.spring.jwt.Exam.repository;

import com.spring.jwt.Exam.entity.ExamSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ExamSessionRepository extends JpaRepository<ExamSession, Integer> {
    List<ExamSession> findByUser_Id(Long userId);

    // Finds the last session for a user, ordered by sessionId descending
    Optional<ExamSession> findTopByUser_IdOrderBySessionIdDesc(Long userId);

//    Optional<ExamSession> findByUser_IdAndPaper_Id(long userId, Integer paperId);

    @Query("SELECT e FROM ExamSession e WHERE e.user.id = :userId AND e.paper.paperId = :paperId")
    Optional<ExamSession> findByUserIdAndPaperId(@Param("userId") long userId, @Param("paperId") Integer paperId);
}
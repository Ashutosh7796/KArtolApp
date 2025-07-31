package com.spring.jwt.Exam.repository;

import com.spring.jwt.Exam.Dto.ExamPaperSummaryDto;
import com.spring.jwt.Exam.entity.ExamSession;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ExamSessionRepository extends JpaRepository<ExamSession, Integer> {
    List<ExamSession> findByUser_Id(Long userId);

    // Finds the last session for a user, ordered by sessionId descending
    Optional<ExamSession> findTopByUser_IdOrderBySessionIdDesc(Long userId);

//    Optional<ExamSession> findByUser_IdAndPaper_Id(long userId, Integer paperId);

    @Query("SELECT e FROM ExamSession e WHERE e.user.id = :userId AND e.paper.paperId = :paperId")
    Optional<ExamSession> findByUserIdAndPaperId(@Param("userId") long userId, @Param("paperId") Integer paperId);
    
    /**
     * Find all exam sessions where the result date is before or equal to the current time
     * @param currentTime The current time to compare with
     * @return List of exam sessions ready for result processing
     */
    @Query("SELECT e FROM ExamSession e WHERE e.resultDate IS NOT NULL AND e.resultDate <= :currentTime")
    List<ExamSession> findByResultDateBeforeOrEqual(@Param("currentTime") LocalDateTime currentTime);
    
    /**
     * Debug method to find all sessions with result dates
     * @return List of exam sessions that have result dates set
     */
    @Query("SELECT e FROM ExamSession e WHERE e.resultDate IS NOT NULL")
    List<ExamSession> findAllWithResultDate();
    
    /**
     * Debug method to find a session by exact result date
     * @param resultDate The exact result date to search for
     * @return List of exam sessions with the specified result date
     */
    @Query("SELECT e FROM ExamSession e WHERE e.resultDate = :resultDate")
    List<ExamSession> findByExactResultDate(@Param("resultDate") LocalDateTime resultDate);
    
    /**
     * Debug method to get raw result date values from the database
     * @return List of raw result date values
     */
    @Query(value = "SELECT session_id, result_date FROM exam_session WHERE result_date IS NOT NULL", nativeQuery = true)
    List<Map<String, Object>> findRawResultDates();

    @Query("SELECT DISTINCT e.paper.paperId FROM ExamSession e")
    List<Integer> findDistinctPaperIds();

    List<ExamSession> findByPaper_PaperId(Integer paperId);



    List<ExamSession> findByPaper_PaperId(Long paperId);

    List<ExamSession> findByUser_IdAndPaper_PaperIdOrderByStartTimeDesc(Integer userId, Integer paperId);



}
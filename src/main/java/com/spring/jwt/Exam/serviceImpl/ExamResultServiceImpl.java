package com.spring.jwt.Exam.serviceImpl;

import com.spring.jwt.Exam.Dto.ExamResultDTO;
import com.spring.jwt.Exam.entity.ExamResult;
import com.spring.jwt.Exam.entity.ExamSession;
import com.spring.jwt.Exam.entity.UserAnswer;
import com.spring.jwt.Exam.repository.ExamResultRepository;
import com.spring.jwt.Exam.repository.ExamSessionRepository;
import com.spring.jwt.Exam.service.ExamResultService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the ExamResultService interface
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ExamResultServiceImpl implements ExamResultService {

    private final ExamSessionRepository examSessionRepository;
    private final ExamResultRepository examResultRepository;
    
    @Override
    @Transactional
    public int processReadyExamResults() {
        LocalDateTime now = LocalDateTime.now();
        List<ExamSession> readySessions = examSessionRepository.findByResultDateBeforeOrEqual(now);
        
        if (readySessions.isEmpty()) {
            log.info("No exam sessions ready for result processing");
            return 0;
        }
        
        log.info("Found {} exam sessions ready for result processing", readySessions.size());
        int processedCount = 0;
        
        for (ExamSession session : readySessions) {
            try {
                processExamSession(session);
                processedCount++;
            } catch (Exception e) {
                log.error("Error processing exam session with ID {}: {}", session.getSessionId(), e.getMessage(), e);
            }
        }
        
        log.info("Successfully processed {} out of {} exam sessions", processedCount, readySessions.size());
        return processedCount;
    }
    
    @Override
    @Transactional
    public ExamResult processExamSession(ExamSession session) {
        log.info("Processing exam session with ID: {}", session.getSessionId());

        int totalQuestions = session.getPaper().getPaperQuestions().size();
        int answeredQuestions = session.getUserAnswers() != null ? session.getUserAnswers().size() : 0;
        int unansweredQuestions = totalQuestions - answeredQuestions;
        
        int correctAnswers = 0;
        int incorrectAnswers = 0;
        
        if (session.getUserAnswers() != null) {
            for (UserAnswer answer : session.getUserAnswers()) {
                if (answer.getSelectedOption() != null && 
                    answer.getSelectedOption().equals(answer.getQuestion().getAnswer())) {
                    correctAnswers++;
                } else {
                    incorrectAnswers++;
                }
            }
        }

        ExamResult result = new ExamResult();
        result.setUser(session.getUser());
        result.setPaper(session.getPaper());
        result.setStudentClass(session.getStudentClass());
        result.setExamStartTime(session.getStartTime());
        result.setExamEndTime(session.getEndTime());
        result.setResultProcessedTime(LocalDateTime.now());
        result.setScore(session.getScore());
        result.setNegativeCount(session.getNegativeCount());
        result.setNegativeScore(session.getNegativeScore());
        result.setTotalQuestions(totalQuestions);
        result.setCorrectAnswers(correctAnswers);
        result.setIncorrectAnswers(incorrectAnswers);
        result.setUnansweredQuestions(unansweredQuestions);
        result.setOriginalSessionId(session.getSessionId());

        ExamResult savedResult = examResultRepository.save(result);
        log.info("Created exam result with ID: {} for session ID: {}", savedResult.getResultId(), session.getSessionId());

        examSessionRepository.delete(session);
        log.info("Deleted exam session with ID: {}", session.getSessionId());
        
        return savedResult;
    }
    
    @Override
    public List<ExamResultDTO> getResultsByUserId(Long userId) {
        List<ExamResult> results = examResultRepository.findByUser_Id(userId);
        return results.stream()
                .map(ExamResultDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<ExamResultDTO> getResultsByPaperId(Integer paperId) {
        List<ExamResult> results = examResultRepository.findByPaper_PaperId(paperId);
        return results.stream()
                .map(ExamResultDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<ExamResultDTO> getResultsByStudentClass(String studentClass) {
        List<ExamResult> results = examResultRepository.findByStudentClass(studentClass);
        return results.stream()
                .map(ExamResultDTO::fromEntity)
                .collect(Collectors.toList());
    }
} 
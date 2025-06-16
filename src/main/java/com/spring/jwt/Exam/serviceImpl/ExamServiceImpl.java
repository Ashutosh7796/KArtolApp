package com.spring.jwt.Exam.serviceImpl;

import com.spring.jwt.Exam.Dto.*;
import com.spring.jwt.Exam.entity.*;
import com.spring.jwt.Exam.repository.*;
import com.spring.jwt.entity.Question;
import com.spring.jwt.entity.User;
import com.spring.jwt.Question.*;
import com.spring.jwt.Exam.service.ExamService;
import com.spring.jwt.exception.ExamTimeWindowException;
import com.spring.jwt.exception.ResourceNotFoundException;
import com.spring.jwt.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExamServiceImpl implements ExamService {

    @Autowired
    private ExamSessionRepository examSessionRepository;
    @Autowired
    private PaperRepository paperRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private UserAnswerRepository userAnswerRepository;
    @Autowired
    private PaperQuestionRepository paperQuestionRepository;

    @Override
    public ExamSessionDTO startExam(Integer userId, Integer paperId, String studentClass) {
        User user = userRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        Paper paper = paperRepository.findById(paperId)
                .orElseThrow(() -> new ResourceNotFoundException("Paper not found with ID: " + paperId));

        LocalDateTime now = LocalDateTime.now();
        // Check exam time window
        if (paper.getStartTime() != null && paper.getEndTime() != null) {
            if (now.isBefore(paper.getStartTime()) || now.isAfter(paper.getEndTime())) {
                throw new ExamTimeWindowException(
                        "Exam can only be started between " + paper.getStartTime() + " and " + paper.getEndTime()
                );
            }
        }

        ExamSession session = new ExamSession();
        session.setUser(user);
        session.setPaper(paper);
        session.setStudentClass(studentClass);
        session.setStartTime(now);
        session.setScore(0);
        session.setUserAnswers(new ArrayList<>());
        ExamSession saved = examSessionRepository.save(session);
        return convertToDTO(saved);
    }

    @Override
    public ExamSessionDTO submitExamAnswers(Integer sessionId, List<UserAnswerDTO> answers) {
        ExamSession session = examSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam session not found with ID: " + sessionId));
        int score = 0;
        List<UserAnswer> userAnswers = new ArrayList<>();
        for (UserAnswerDTO dto : answers) {
            Question question = questionRepository.findById(dto.getQuestionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Question not found with ID: " + dto.getQuestionId()));
            UserAnswer ua = new UserAnswer();
            ua.setExamSession(session);
            ua.setQuestion(question);
            ua.setSelectedOption(dto.getSelectedOption());
            userAnswers.add(ua);

            if (question.getAnswer().equalsIgnoreCase(dto.getSelectedOption())) {
                try {
                    score += Integer.parseInt(question.getMarks());
                } catch (NumberFormatException nfe) {
                    // Optionally log invalid marks format
                }
            }
        }
        session.setEndTime(LocalDateTime.now());
        session.setScore(score);
        session.setUserAnswers(userAnswers);
        examSessionRepository.save(session);
        return convertToDTO(session);
    }

    private ExamSessionDTO convertToDTO(ExamSession session) {
        ExamSessionDTO dto = new ExamSessionDTO();
        dto.setSessionId(session.getSessionId());
        dto.setUserId(session.getUser().getId());
        dto.setPaperId(session.getPaper().getPaperId());
        dto.setStartTime(session.getStartTime());
        dto.setEndTime(session.getEndTime());
        dto.setScore(session.getScore());
        dto.setStudentClass(session.getStudentClass());
        dto.setUserAnswers(
                session.getUserAnswers() != null ?
                        session.getUserAnswers().stream().map(ua -> {
                            UserAnswerDTO uadto = new UserAnswerDTO();
                            uadto.setId(ua.getId());
                            uadto.setSessionId(session.getSessionId());
                            uadto.setQuestionId(ua.getQuestion().getQuestionId());
                            uadto.setSelectedOption(ua.getSelectedOption());
                            return uadto;
                        }).collect(Collectors.toList()) : new ArrayList<>()
        );
        return dto;
    }

    @Override
    public List<ExamResultDTO> getResultsByUserId(Long userId) {
        // Check that the user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        try {
            List<ExamSession> sessions = examSessionRepository.findByUser_Id(userId);
            // You can choose to throw if sessions.isEmpty(), but returning an empty list is RESTful
            return sessions.stream().map(session -> {
                ExamResultDTO dto = new ExamResultDTO();
                dto.setSessionId(session.getSessionId());
                dto.setPaperId(session.getPaper().getPaperId());
                dto.setPaperTitle(session.getPaper().getTitle());
                dto.setScore(session.getScore());
                dto.setStudentClass(session.getStudentClass());
                dto.setStatus(session.getEndTime() != null ? "Submitted" : "In Progress");
                return dto;
            }).collect(Collectors.toList());
        } catch (Exception ex) {
            throw new RuntimeException("Failed to fetch exam results for userId: " + userId, ex);
        }
    }

    @Override
    public List<SessionQuestionAnswerDTO> getQuestionsAndAnswersBySessionId(Integer sessionId) {
        ExamSession session = examSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found: " + sessionId));

        return session.getUserAnswers().stream().map(userAnswer -> {
            SessionQuestionAnswerDTO dto = new SessionQuestionAnswerDTO();
            dto.setQuestionId(Long.valueOf(userAnswer.getQuestion().getQuestionId()));
            dto.setQuestionText(userAnswer.getQuestion().getQuestionText());
            dto.setOptions(new String[]{
                    userAnswer.getQuestion().getOption1(),
                    userAnswer.getQuestion().getOption2(),
                    userAnswer.getQuestion().getOption3(),
                    userAnswer.getQuestion().getOption4()
            });
            dto.setSubmittedAnswer(userAnswer.getSelectedOption());
            // Uncomment below if you want to include the correct answer:
            // dto.setCorrectAnswer(userAnswer.getQuestion().getAnswer());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<StudentClassResultDTO> getResultsGroupedByStudentClass() {
        List<ExamSession> sessions = examSessionRepository.findAll();

        // Group by studentClass
        Map<String, List<ExamSession>> grouped = sessions.stream()
                .collect(Collectors.groupingBy(ExamSession::getStudentClass));

        // Map to DTO
        return grouped.entrySet().stream().map(entry -> {
            StudentClassResultDTO dto = new StudentClassResultDTO();
            dto.setStudentClass(entry.getKey());
            List<ExamResultDTO> resultDTOs = entry.getValue().stream().map(session -> {
                ExamResultDTO res = new ExamResultDTO();
                res.setSessionId(session.getSessionId());
                res.setPaperId(session.getPaper().getPaperId());
                res.setPaperTitle(session.getPaper().getTitle());
                res.setScore(session.getScore());
                res.setStudentClass(session.getStudentClass());
                res.setStatus(session.getEndTime() != null ? "Submitted" : "In Progress");
                return res;
            }).collect(Collectors.toList());
            dto.setResults(resultDTOs);
            return dto;
        }).collect(Collectors.toList());
    }
}
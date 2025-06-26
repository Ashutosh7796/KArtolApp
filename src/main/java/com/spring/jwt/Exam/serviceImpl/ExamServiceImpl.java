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
    public PaperWithQuestionsDTOn startExam(Integer userId, Integer paperId, String studentClass) {
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
        ExamSession savedSession = examSessionRepository.save(session); // Save and get the sessionId

        // Map Paper and its Questions to DTO
        PaperWithQuestionsDTOn dto = new PaperWithQuestionsDTOn();
        dto.setSessionId(savedSession.getSessionId()); // Set sessionId here!
        dto.setPaperId(paper.getPaperId());
        dto.setTitle(paper.getTitle());
        dto.setDescription(paper.getDescription());
        dto.setStartTime(paper.getStartTime());
        dto.setEndTime(paper.getEndTime());
        dto.setIsLive(paper.getIsLive());
        dto.setStudentClass(studentClass);

        List<QuestionNoAnswerDTO> questionDTOs = paper.getPaperQuestions().stream()
                .map(PaperQuestion::getQuestion)
                .map(this::convertToQuestionNoAnswerDTO)
                .collect(Collectors.toList());
        dto.setQuestions(questionDTOs);
        return dto;
    }
    // Updated conversion method for Question to QuestionNoAnswerDTO
    private QuestionNoAnswerDTO convertToQuestionNoAnswerDTO(Question question) {
        QuestionNoAnswerDTO dto = new QuestionNoAnswerDTO();
        dto.setQuestionId(question.getQuestionId());
        dto.setQuestionText(question.getQuestionText());
        dto.setType(question.getType());
        dto.setSubject(question.getSubject());
        dto.setLevel(question.getLevel());
        dto.setMarks(question.getMarks());
        dto.setUserId(question.getUserId());
        dto.setOption1(question.getOption1());
        dto.setOption2(question.getOption2());
        dto.setOption3(question.getOption3());
        dto.setOption4(question.getOption4());
        dto.setStudentClass(question.getStudentClass());
        return dto;
    }

//    @Override
//    public ExamSessionDTO startExam(Integer userId, Integer paperId, String studentClass) {
//        User user = userRepository.findById(Long.valueOf(userId))
//                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
//        Paper paper = paperRepository.findById(paperId)
//                .orElseThrow(() -> new ResourceNotFoundException("Paper not found with ID: " + paperId));
//
//        LocalDateTime now = LocalDateTime.now();
//        // Check exam time window
//        if (paper.getStartTime() != null && paper.getEndTime() != null) {
//            if (now.isBefore(paper.getStartTime()) || now.isAfter(paper.getEndTime())) {
//                throw new ExamTimeWindowException(
//                        "Exam can only be started between " + paper.getStartTime() + " and " + paper.getEndTime()
//                );
//            }
//        }
//
//        ExamSession session = new ExamSession();
//        session.setUser(user);
//        session.setPaper(paper);
//        session.setStudentClass(studentClass);
//        session.setStartTime(now);
//        session.setScore(0);
//        session.setUserAnswers(new ArrayList<>());
//        ExamSession saved = examSessionRepository.save(session);
//        return convertToDTO(saved);
//    }


@Override
public ExamSessionDTO submitExamAnswers(Integer sessionId, Long userId, List<UserAnswerDTO> answers) {
    ExamSession session = examSessionRepository.findById(sessionId)
            .orElseThrow(() -> new ResourceNotFoundException("Exam session not found with ID: " + sessionId));

    // Log a warning if the userId does not match the session owner, but continue processing
    if (!session.getUser().getId().equals(userId)) {
        System.out.println("Warning: User ID " + userId + " is submitting for session owned by user " + session.getUser().getId());
        // Optionally: you could return here or throw, but you chose to process anyway
    }

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

        // Score calculation
        if (question.getAnswer() != null && question.getAnswer().equalsIgnoreCase(dto.getSelectedOption())) {
            try {
                score += Integer.parseInt(question.getMarks());
            } catch (NumberFormatException nfe) {
                // Optionally log invalid marks format
                System.out.println("Invalid marks format for question ID: " + question.getQuestionId());
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

    @Override
    public List<ExamSessionDTO> getAllExamSessions() {
        try {
            List<ExamSession> sessions = examSessionRepository.findAll();
            return sessions.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            // You could use your own custom exception type if you want
            throw new RuntimeException("Failed to fetch all exam sessions", ex);
        }
    }

    @Override
    public ExamSessionDTO getLastExamSessionByUserId(Long userId) {
        ExamSession session = examSessionRepository.findTopByUser_IdOrderBySessionIdDesc(userId)
                .orElseThrow(() -> new ResourceNotFoundException("No exam session found for user: " + userId));
        return convertToDTO(session);
    }
}
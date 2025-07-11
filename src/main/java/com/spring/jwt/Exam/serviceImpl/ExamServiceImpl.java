package com.spring.jwt.Exam.serviceImpl;

import com.spring.jwt.Exam.Dto.*;
import com.spring.jwt.Exam.entity.*;
import com.spring.jwt.Exam.repository.*;
import com.spring.jwt.dto.ResponseDto;
import com.spring.jwt.entity.PaperPattern;
import com.spring.jwt.entity.Question;
import com.spring.jwt.entity.User;
import com.spring.jwt.Question.*;
import com.spring.jwt.Exam.service.ExamService;
import com.spring.jwt.Exam.service.ExamSessionSchedulingService;
import com.spring.jwt.exception.ExamTimeWindowException;
import com.spring.jwt.exception.ResourceNotFoundException;
import com.spring.jwt.repository.UserRepository;
import com.spring.jwt.PaperPattern.PaperPatternRepository;
import jakarta.transaction.Transactional;
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
    @Autowired
    private DescriptiveAnsRepository descriptiveAnsRepository;
    @Autowired
    private PaperPatternRepository paperPatternRepository;
    @Autowired
    private ExamSessionSchedulingService examSessionSchedulingService;
    @Autowired
    private NegativeMarksRepository negativeMarksRepository;


    private QuestionNoAnswerDTO convertToQuestionNoAnswerDTO(Question question) {
        if (question == null) {
            throw new IllegalArgumentException("Question cannot be null");
        }

        QuestionNoAnswerDTO dto = new QuestionNoAnswerDTO();
        dto.setQuestionId(question.getQuestionId());
        dto.setQuestionText(question.getQuestionText());
        dto.setType(question.getType());
        dto.setSubject(question.getSubject());
        dto.setLevel(question.getLevel());
        dto.setMarks(question.getMarks());
        dto.setMultiOptions(question.isMultiOptions());

        // Set options (can include null checks if needed)
        dto.setOption1(question.getOption1());
        dto.setOption2(question.getOption2());
        dto.setOption3(question.getOption3());
        dto.setOption4(question.getOption4());

        // Descriptive flag
        dto.setDescriptive(question.isDescriptive());

        return dto;
    }



    @Override
    public PaperWithQuestionsDTOn startExam(Integer userId, Integer paperId, String studentClass) {
        User user = userRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        Paper paper = paperRepository.findById(paperId)
                .orElseThrow(() -> new ResourceNotFoundException("Paper not found with ID: " + paperId));

        LocalDateTime now = LocalDateTime.now();

        // Check for existing session
        Optional<ExamSession> existingSessionOpt = examSessionRepository
                .findByUserIdAndPaperId(userId, paperId);

        if (existingSessionOpt.isPresent()) {
            ExamSession existingSession = existingSessionOpt.get();
            if (paper.getEndTime() != null && now.isAfter(paper.getEndTime())) {
                throw new ExamTimeWindowException("Exam time is over. You cannot resume the exam.");
            }

            // Return existing session details
            PaperWithQuestionsDTOn dto = new PaperWithQuestionsDTOn();
            dto.setSessionId(existingSession.getSessionId());
            dto.setPaperId(paper.getPaperId());
            dto.setTitle(paper.getTitle());
            dto.setDescription(paper.getDescription());
            dto.setStartTime(paper.getStartTime());
            dto.setEndTime(paper.getEndTime());
            dto.setIsLive(paper.getIsLive());
            dto.setStudentClass(existingSession.getStudentClass());
            if (paper.getPaperPattern() != null) {
                dto.setPaperPatternId(paper.getPaperPattern().getPaperPatternId());
            }

            List<QuestionNoAnswerDTO> questionDTOs = paper.getPaperQuestions().stream()
                    .map(PaperQuestion::getQuestion)
                    .map(this::convertToQuestionNoAnswerDTO)
                    .collect(Collectors.toList());
            Collections.shuffle(questionDTOs);
            dto.setQuestions(questionDTOs);
            return dto;
        }

        // If no session exists, create new one
        if (paper.getStartTime() != null && paper.getEndTime() != null) {
            if (now.isBefore(paper.getStartTime()) || now.isAfter(paper.getEndTime())) {
                throw new ExamTimeWindowException("Exam can only be started between " + paper.getStartTime() + " and " + paper.getEndTime());
            }
        }

        ExamSession session = new ExamSession();
        session.setUser(user);
        session.setPaper(paper);
        session.setStudentClass(studentClass);
        session.setStartTime(now);
        session.setScore(0.0);
        session.setUserAnswers(new ArrayList<>());
        ExamSession savedSession = examSessionRepository.save(session);
        
        // Schedule result processing if paper has an end time
        if (paper.getEndTime() != null) {
            // Schedule result processing for 5 minutes after the paper end time
            LocalDateTime resultDateTime = paper.getEndTime().plusMinutes(5);
            examSessionSchedulingService.scheduleExamResultProcessing(savedSession.getSessionId(), resultDateTime);
        }

        PaperWithQuestionsDTOn dto = new PaperWithQuestionsDTOn();
        dto.setSessionId(savedSession.getSessionId());
        dto.setPaperId(paper.getPaperId());
        dto.setTitle(paper.getTitle());
        dto.setDescription(paper.getDescription());
        dto.setStartTime(paper.getStartTime());
        dto.setEndTime(paper.getEndTime());
        dto.setIsLive(paper.getIsLive());
        dto.setStudentClass(studentClass);
        dto.setPaperPatternId(paper.getPaperPattern().getPaperPatternId());

        List<QuestionNoAnswerDTO> questionDTOs = paper.getPaperQuestions().stream()
                .map(PaperQuestion::getQuestion)
                .map(this::convertToQuestionNoAnswerDTO)
                .collect(Collectors.toList());
        Collections.shuffle(questionDTOs);
        dto.setQuestions(questionDTOs);
        return dto;
    }


//
//    @Override
//    public PaperWithQuestionsDTOn startExam(Integer userId, Integer paperId, String studentClass) {
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
//        session.setScore((double) 0);
//        session.setUserAnswers(new ArrayList<>());
//        ExamSession savedSession = examSessionRepository.save(session); // Save and get the sessionId
//
//        // Map Paper and its Questions to DTO
//        PaperWithQuestionsDTOn dto = new PaperWithQuestionsDTOn();
//        dto.setSessionId(savedSession.getSessionId()); // Set sessionId here!
//        dto.setPaperId(paper.getPaperId());
//        dto.setTitle(paper.getTitle());
//        dto.setDescription(paper.getDescription());
//        dto.setStartTime(paper.getStartTime());
//        dto.setEndTime(paper.getEndTime());
//        dto.setIsLive(paper.getIsLive());
//        dto.setStudentClass(studentClass);
//        dto.setPaperPatternId(paper.getPaperPatternId());
//
//        List<QuestionNoAnswerDTO> questionDTOs = paper.getPaperQuestions().stream()
//                .map(PaperQuestion::getQuestion)
//                .map(this::convertToQuestionNoAnswerDTO)
//                .collect(Collectors.toList());
//        dto.setQuestions(questionDTOs);
//        return dto;
//    }
//    // Updated conversion method for Question to QuestionNoAnswerDTO
//    private QuestionNoAnswerDTO convertToQuestionNoAnswerDTO(Question question) {
//        QuestionNoAnswerDTO dto = new QuestionNoAnswerDTO();
//        dto.setQuestionId(question.getQuestionId());
//        dto.setQuestionText(question.getQuestionText());
//        dto.setType(question.getType());
//        dto.setSubject(question.getSubject());
//        dto.setLevel(question.getLevel());
//        dto.setMarks(question.getMarks());
//        dto.setUserId(question.getUserId());
//        dto.setOption1(question.getOption1());
//        dto.setOption2(question.getOption2());
//        dto.setOption3(question.getOption3());
//        dto.setOption4(question.getOption4());
//        dto.setDescriptive(question.isDescriptive());
//        dto.setStudentClass(question.getStudentClass());
//        return dto;
//    }


    private ExamSessionDTO convertToDTO(ExamSession session) {
        if (session == null) return null;

        ExamSessionDTO dto = new ExamSessionDTO();
        dto.setSessionId(session.getSessionId());
        dto.setUserId(session.getUser().getId().intValue());
        dto.setPaperId(session.getPaper().getPaperId());
        dto.setStartTime(session.getStartTime());
        dto.setEndTime(session.getEndTime());
        dto.setScore(session.getScore());
        dto.setStudentClass(session.getStudentClass());
        dto.setNegativeCount(session.getNegativeCount());
        dto.setNegativeScore(session.getNegativeScore());

        List<UserAnswerDTO> uaDTOs = session.getUserAnswers() != null
                ? session.getUserAnswers().stream().map(ua -> {
            UserAnswerDTO uaDTO = new UserAnswerDTO();
            uaDTO.setQuestionId(ua.getQuestion().getQuestionId());
            uaDTO.setSelectedOption(ua.getSelectedOption());
            return uaDTO;
        }).toList() : new ArrayList<>();

        dto.setUserAnswers(uaDTOs);

        return dto;
    }
//    @Override
//    @Transactional
//    public ResponseDto1<Double> submitExamAnswers(Integer sessionId, Long userId, List<UserAnswerDTO> answers) {
//        try {
//            ExamSession session = examSessionRepository.findById(sessionId)
//                    .orElseThrow(() -> new ResourceNotFoundException("Exam session not found with ID: " + sessionId));
//
//            if (!session.getUser().getId().equals(userId)) {
//                System.out.println("Warning: User ID " + userId + " is submitting for session owned by user " + session.getUser().getId());
//            }
//
//            double score = 0;
//            double negativeScore = 0.0;
//            int negativeCount = 0;
//
//            List<UserAnswer> userAnswers = new ArrayList<>();
//
//            Paper paper = session.getPaper();
//            Integer patternId = paper.getPaperPattern() != null ? paper.getPaperPattern().getPaperPatternId() : null;
//
//            PaperPattern pattern = patternId != null
//                    ? paperPatternRepository.findById(patternId).orElse(null)
//                    : null;
//
//            int negativeType = pattern != null ? pattern.getNegativeMarks() : 0;
//            double negativePerWrong = switch (negativeType) {
//                case 1 -> 0.25;
//                case 2 -> 0.50;
//                case 3 -> 0.75;
//                case 4 -> 1.0;
//                default -> 0.0;
//            };
//
//
//            for (UserAnswerDTO dto : answers) {
//                Question question = questionRepository.findById(dto.getQuestionId())
//                        .orElseThrow(() -> new ResourceNotFoundException("Question not found with ID: " + dto.getQuestionId()));
//
//                if (question.isDescriptive()) {
//                    DescriptiveAns da = new DescriptiveAns();
//                    da.setQuestionId(question.getQuestionId());
//                    da.setPaperId(paper.getPaperId());
//                    da.setUserId(userId.intValue());
//                    da.setAns(dto.getSelectedOption());
//                    descriptiveAnsRepository.save(da);
//                } else {
//                    UserAnswer ua = new UserAnswer();
//                    ua.setExamSession(session);
//                    ua.setQuestion(question);
//                    ua.setSelectedOption(dto.getSelectedOption());
//                    userAnswers.add(ua);
//
//                    boolean isCorrect = question.getAnswer() != null &&
//                            question.getAnswer().equalsIgnoreCase(dto.getSelectedOption());
//
//                    if (isCorrect) {
//                        score += question.getMarks();
//                    } else {
//                        negativeCount++;
//
//                        // First check if specific negative mark exists for this paper/question
//                        Optional<NegativeMarks> negative = negativeMarksRepository.findByPaperAndQuestionId(paper, question.getQuestionId());
//
//                        double questionNegative = negative.map(NegativeMarks::getNegativeMark)
//                                .orElse(question.getMarks() * negativePerWrong); // fallback to pattern logic
//
//                        negativeScore += questionNegative;
//                    }
//                }
//            }
//
//
//
//            double finalScore = score - negativeScore;
//            session.setEndTime(LocalDateTime.now());
//            session.setScore((double) Math.max(0, Math.round(finalScore)));
//            session.setUserAnswers(userAnswers);
//            session.setNegativeCount((double) negativeCount);
//            session.setNegativeScore(negativeScore);
//            session.setResultDate(paper.getResultDate());
//
//            examSessionRepository.save(session);
//
//            return ResponseDto1.success(
//                    "Exam submitted successfully",
//                    session.getPaper().getPaperId(),
//                    session.getStartTime().toLocalDate(),
//                    session.getEndTime().toLocalDate()
//            );
//        } catch (Exception e) {
//            return ResponseDto1.error("Failed to submit exam", e.getMessage());
//        }
//    }

    @Override
    @Transactional
    public ResponseDto1<Double> submitExamAnswers(Integer sessionId, Long userId, List<UserAnswerDTO> answers) {
        try {
            ExamSession session = examSessionRepository.findById(sessionId)
                    .orElseThrow(() -> new ResourceNotFoundException("Exam session not found with ID: " + sessionId));

            if (!session.getUser().getId().equals(userId)) {
                System.out.println("Warning: User ID " + userId + " is submitting for session owned by user " + session.getUser().getId());
            }

            double score = 0;
            double negativeScore = 0.0;
            int negativeCount = 0;

            List<UserAnswer> userAnswers = new ArrayList<>();

            Paper paper = session.getPaper();
            Integer patternId = paper.getPaperPattern() != null ? paper.getPaperPattern().getPaperPatternId() : null;

            PaperPattern pattern = patternId != null
                    ? paperPatternRepository.findById(patternId).orElse(null)
                    : null;

            int negativeType = pattern != null ? pattern.getNegativeMarks() : 0;
            double negativePerWrong = switch (negativeType) {
                case 1 -> 0.25;
                case 2 -> 0.50;
                case 3 -> 0.75;
                case 4 -> 1.0;
                default -> 0.0;
            };

            for (UserAnswerDTO dto : answers) {
                Question question = questionRepository.findById(dto.getQuestionId())
                        .orElseThrow(() -> new ResourceNotFoundException("Question not found with ID: " + dto.getQuestionId()));

                String selectedAns = dto.getSelectedOption();
                String correctAns = question.getAnswer();

                boolean isMultiOptions = question.isMultiOptions();

                if (question.isDescriptive()) {
                    // Save descriptive answer
                    DescriptiveAns da = new DescriptiveAns();
                    da.setQuestionId(question.getQuestionId());
                    da.setPaperId(paper.getPaperId());
                    da.setUserId(userId.intValue());
                    da.setAns(selectedAns);
                    descriptiveAnsRepository.save(da);
                } else {
                    // Save objective answer
                    UserAnswer ua = new UserAnswer();
                    ua.setExamSession(session);
                    ua.setQuestion(question);
                    ua.setSelectedOption(selectedAns);
                    userAnswers.add(ua);
                }

                double questionMarks = question.getMarks();

                if (correctAns != null && selectedAns != null && isMultiOptions) {
                    Set<Character> correctSet = correctAns.chars().mapToObj(c -> (char) c).collect(Collectors.toSet());
                    Set<Character> selectedSet = selectedAns.chars().mapToObj(c -> (char) c).collect(Collectors.toSet());

                    if (selectedSet.equals(correctSet)) {
                        // Full match, any order
                        score += questionMarks;
                    } else if (correctSet.containsAll(selectedSet)) {
                        // All selected options are correct, but not all correct options selected → Half marks
                        score += questionMarks / 2.0;
                    } else {
                        // At least one wrong/extra option present → -2 marks
                        negativeScore += 2.0;
                        negativeCount++;
                    }

                } else {
                    // Non-multi-option or regular correctness
                    boolean isCorrect = correctAns != null && correctAns.equalsIgnoreCase(selectedAns);

                    if (isCorrect) {
                        score += questionMarks;
                    } else {
                        negativeCount++;

                        Optional<NegativeMarks> negative = negativeMarksRepository.findByPaperAndQuestionId(paper, question.getQuestionId());

                        double questionNegative = negative.map(NegativeMarks::getNegativeMark)
                                .orElse(question.getMarks() * negativePerWrong);

                        negativeScore += questionNegative;
                    }
                }
            }


            double finalScore = score - negativeScore;
            session.setEndTime(LocalDateTime.now());
            session.setScore((double) Math.round(finalScore)); // allows negative scores
            session.setUserAnswers(userAnswers);
            session.setNegativeCount((double) negativeCount);
            session.setNegativeScore(negativeScore);
            session.setResultDate(paper.getResultDate());

            examSessionRepository.save(session);


            return ResponseDto1.success(
                    "Exam submitted successfully",
                    session.getPaper().getPaperId(),
                    session.getStartTime().toLocalDate(),
                    session.getEndTime().toLocalDate()
            );
        } catch (Exception e) {
            return ResponseDto1.error("Failed to submit exam", e.getMessage());
        }
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
                // Use resultId instead of sessionId
                dto.setResultId(session.getSessionId());
                dto.setUserId(session.getUser().getId().intValue());
                dto.setPaperId(session.getPaper().getPaperId());
                dto.setPaperTitle(session.getPaper().getTitle());
                dto.setScore(session.getScore());
                dto.setStudentClass(session.getStudentClass());
                dto.setExamStartTime(session.getStartTime());
                dto.setExamEndTime(session.getEndTime());
                dto.setResultProcessedTime(LocalDateTime.now());
                dto.setNegativeCount(session.getNegativeCount());
                dto.setNegativeScore(session.getNegativeScore());
                
                // Calculate analytics data
                int totalQuestions = session.getPaper().getPaperQuestions().size();
                int answeredQuestions = session.getUserAnswers() != null ? session.getUserAnswers().size() : 0;
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
                
                dto.setTotalQuestions(totalQuestions);
                dto.setCorrectAnswers(correctAnswers);
                dto.setIncorrectAnswers(incorrectAnswers);
                dto.setUnansweredQuestions(totalQuestions - answeredQuestions);
                dto.setOriginalSessionId(session.getSessionId());
                
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
                // Use resultId instead of sessionId
                res.setResultId(session.getSessionId());
                res.setUserId(session.getUser().getId().intValue());
                res.setPaperId(session.getPaper().getPaperId());
                res.setPaperTitle(session.getPaper().getTitle());
                res.setScore(session.getScore());
                res.setStudentClass(session.getStudentClass());
                res.setExamStartTime(session.getStartTime());
                res.setExamEndTime(session.getEndTime());
                res.setResultProcessedTime(LocalDateTime.now());
                res.setNegativeCount(session.getNegativeCount());
                res.setNegativeScore(session.getNegativeScore());
                
                // Calculate analytics data
                int totalQuestions = session.getPaper().getPaperQuestions().size();
                int answeredQuestions = session.getUserAnswers() != null ? session.getUserAnswers().size() : 0;
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
                
                res.setTotalQuestions(totalQuestions);
                res.setCorrectAnswers(correctAnswers);
                res.setIncorrectAnswers(incorrectAnswers);
                res.setUnansweredQuestions(totalQuestions - answeredQuestions);
                res.setOriginalSessionId(session.getSessionId());
                
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

    @Override
    public List<ExamPaperSummaryDto> getAllUniquePapers() {
        List<ExamPaperSummaryDto> papers = examSessionRepository.findAllUniquePapers();
        if (papers.isEmpty()) {
            throw new ResourceNotFoundException("No exam papers found.");
        }
        return papers;
    }

}
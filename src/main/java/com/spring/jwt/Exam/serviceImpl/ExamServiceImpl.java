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

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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

        // ✅ Fetch latest existing session if available
        List<ExamSession> sessions = examSessionRepository.findByUser_IdAndPaper_PaperIdOrderByStartTimeDesc(userId, paperId);
        if (!sessions.isEmpty()) {
            ExamSession existingSession = sessions.get(0); // latest session

            // ✅ Case: Exam already submitted
            if (existingSession.getScore() != null) {
                throw new ExamTimeWindowException("Exam already submitted by you.");
            }

            // ✅ Case: Exam is ongoing
            if (paper.getEndTime() != null && now.isAfter(paper.getEndTime())) {
                throw new ExamTimeWindowException("Exam time is over. You cannot resume the exam.");
            }

            PaperWithQuestionsDTOn dto = new PaperWithQuestionsDTOn();
            dto.setSessionId(existingSession.getSessionId());
            dto.setPaperId(paper.getPaperId());
            dto.setTitle(paper.getTitle());
            dto.setDescription(paper.getDescription());
            dto.setStartTime(paper.getStartTime());
            dto.setEndTime(paper.getEndTime());
            dto.setIsLive(paper.getIsLive());
            dto.setPaperEndTime(paper.getPaperEndTime());

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

        // ✅ No session yet — check time window
        if (paper.getStartTime() != null && paper.getPaperEndTime() != null) {
            if (now.isBefore(paper.getStartTime()) || now.isAfter(paper.getPaperEndTime())) {
                throw new ExamTimeWindowException("Exam can only be started between " + paper.getStartTime() + " and " + paper.getPaperEndTime());
            }
        }

        // ✅ Create new session
        ExamSession session = new ExamSession();
        session.setUser(user);
        session.setPaper(paper);
        session.setStudentClass(studentClass);
        session.setStartTime(now);
        session.setScore(null); // will be updated on submission
        session.setUserAnswers(new ArrayList<>());
        ExamSession savedSession = examSessionRepository.save(session);

        // Schedule result processing
        if (paper.getEndTime() != null) {
            LocalDateTime resultDateTime = paper.getEndTime().plusMinutes(5);
            examSessionSchedulingService.scheduleExamResultProcessing(savedSession.getSessionId(), resultDateTime);
        }

        // Return DTO
        PaperWithQuestionsDTOn dto = new PaperWithQuestionsDTOn();
        dto.setSessionId(savedSession.getSessionId());
        dto.setPaperId(paper.getPaperId());
        dto.setTitle(paper.getTitle());
        dto.setDescription(paper.getDescription());
        dto.setStartTime(paper.getStartTime());
        dto.setEndTime(paper.getEndTime());
        dto.setIsLive(paper.getIsLive());
        dto.setPaperEndTime(paper.getPaperEndTime());
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
//    @Override
//    public PaperWithQuestionsDTOn startExamMobile(Integer userId, Integer paperId, String studentClass) {
//        User user = userRepository.findById(Long.valueOf(userId))
//                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
//
//        Paper paper = paperRepository.findById(paperId)
//                .orElseThrow(() -> new ResourceNotFoundException("Paper not found with ID: " + paperId));
//
//        LocalDateTime now = LocalDateTime.now();
//
//        // Allow start 10 minutes before official start time
//        LocalDateTime allowedStartTime = paper.getStartTime() != null ? paper.getStartTime().minusMinutes(10) : null;
//
//        // Fetch latest existing session if available
//        List<ExamSession> sessions = examSessionRepository.findByUser_IdAndPaper_PaperIdOrderByStartTimeDesc(userId, paperId);
//        if (!sessions.isEmpty()) {
//            ExamSession existingSession = sessions.get(0);
//
//            if (existingSession.getScore() != null) {
//                throw new ExamTimeWindowException("Exam already submitted by you.");
//            }
//
//            if (paper.getEndTime() != null && now.isAfter(paper.getEndTime())) {
//                throw new ExamTimeWindowException("Exam time is over. You cannot resume the exam.");
//            }
//
//            return buildExamDTO(existingSession, paper, existingSession.getStudentClass());
//        }
//
//        // Check time window with 10-minute earlier start allowed for mobile
//        if (allowedStartTime != null && paper.getEndTime() != null) {
//            if (now.isBefore(allowedStartTime) || now.isAfter(paper.getEndTime())) {
//                throw new ExamTimeWindowException("Exam can only be started between " + allowedStartTime + " and " + paper.getEndTime());
//            }
//        }
//
//        // Create new session
//        ExamSession session = new ExamSession();
//        session.setUser(user);
//        session.setPaper(paper);
//        session.setStudentClass(studentClass);
//        session.setStartTime(now);
//        session.setScore(null);
//        session.setUserAnswers(new ArrayList<>());
//        ExamSession savedSession = examSessionRepository.save(session);
//
//        // Schedule result processing
//        if (paper.getEndTime() != null) {
//            LocalDateTime resultDateTime = paper.getEndTime().plusMinutes(5);
//            examSessionSchedulingService.scheduleExamResultProcessing(savedSession.getSessionId(), resultDateTime);
//        }
//
//        return buildExamDTO(savedSession, paper, studentClass);
//    }
@Override
@Transactional // ensures consistency & prevents race condition
public PaperWithQuestionsDTOnn startExamMobile(Integer userId, Integer paperId, String studentClass) {
    User user = userRepository.findById(Long.valueOf(userId))
            .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

    Paper paper = paperRepository.findById(paperId)
            .orElseThrow(() -> new ResourceNotFoundException("Paper not found with ID: " + paperId));

    LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));

    LocalDateTime allowedStartTime = paper.getStartTime() != null
            ? paper.getStartTime().minusMinutes(10)
            : null;

    ExamSession existingSession = examSessionRepository
            .findByUser_IdAndPaper_PaperIdOrderByStartTimeDesc(userId, paperId)
            .stream()
            .findFirst()
            .orElse(null);

    if (existingSession != null) {
        if (existingSession.getScore() != null) {
            throw new ExamTimeWindowException("Exam already submitted by you.");
        }
        if (paper.getPaperEndTime() != null && now.isAfter(paper.getPaperEndTime())) {
            throw new ExamTimeWindowException("Exam time is over. You cannot resume the exam.");
        }
        if (allowedStartTime != null && now.isBefore(allowedStartTime)) {
            throw new ExamTimeWindowException("You cannot resume the exam before " + allowedStartTime);
        }
        return buildExamDTO(existingSession, paper, existingSession.getStudentClass());
    }

    if (allowedStartTime != null && paper.getPaperEndTime() != null) {
        if (now.isBefore(allowedStartTime) || now.isAfter(paper.getPaperEndTime())) {
            throw new ExamTimeWindowException("Exam can only be started between " + allowedStartTime + " and " + paper.getPaperEndTime());
        }
    } else if (paper.getStartTime() != null && now.isBefore(paper.getStartTime())) {
        throw new ExamTimeWindowException("Exam has not started yet.");
    } else if (paper.getPaperEndTime() != null && now.isAfter(paper.getPaperEndTime())) {
        throw new ExamTimeWindowException("Exam has already ended.");
    }

    ExamSession newSession = new ExamSession();
    newSession.setUser(user);
    newSession.setPaper(paper);
    newSession.setStudentClass(studentClass);
    newSession.setStartTime(now);
    newSession.setScore(null);
    newSession.setUserAnswers(new ArrayList<>());
    ExamSession savedSession = examSessionRepository.save(newSession);

    if (paper.getPaperEndTime() != null && paper.getPaperEndTime().isAfter(now)) {
        examSessionSchedulingService.scheduleExamResultProcessing(savedSession.getSessionId(),
                paper.getEndTime().plusMinutes(5));
    }

    return buildExamDTO(savedSession, paper, studentClass);
}

    public PaperWithQuestionsDTOnn buildExamDTO(ExamSession session, Paper paper, String studentClass) {
        PaperWithQuestionsDTOnn dto = new PaperWithQuestionsDTOnn();
        dto.setSessionId(session.getSessionId());
        dto.setPaperId(paper.getPaperId());
        dto.setTitle(paper.getTitle());
        dto.setDescription(paper.getDescription());
        dto.setStartTime(paper.getStartTime());
        dto.setEndTime(paper.getEndTime());
        dto.setIsLive(paper.getIsLive());
        dto.setStudentClass(studentClass);
        dto.setPaperPatternId(paper.getPaperPattern() != null ? paper.getPaperPattern().getPaperPatternId() : null);
        dto.setPaperEndTime(paper.getPaperEndTime());

        List<QuestionNoAnswerDTO> questionDTOs = paper.getPaperQuestions().stream()
                .map(PaperQuestion::getQuestion)
                .map(this::convertToQuestionNoAnswerDTO)
                .collect(Collectors.toList());

        Collections.shuffle(questionDTOs);
        dto.setQuestions(questionDTOs);

        if (paper.getStartTime() != null && paper.getEndTime() != null) {
            Duration duration = Duration.between(paper.getStartTime(), paper.getEndTime());
            String formattedDuration = String.format("%02d:%02d:%02d",
                    duration.toHours(),
                    duration.toMinutesPart(),
                    duration.toSecondsPart());
            dto.setDuration(formattedDuration);
        } else {
            dto.setDuration("00:00:00");
        }
        return dto;
    }


    private PaperWithQuestionsDTOn buildExamDTOForStart(ExamSession session, Paper paper, String studentClass) {
        PaperWithQuestionsDTOn dto = new PaperWithQuestionsDTOn();

        // Session and paper metadata
        dto.setSessionId(session.getSessionId());
        dto.setPaperId(paper.getPaperId());
        dto.setTitle(paper.getTitle());
        dto.setDescription(paper.getDescription());
        dto.setStartTime(paper.getStartTime());
        dto.setEndTime(paper.getEndTime());
        dto.setIsLive(paper.getIsLive());
        dto.setPaperEndTime(paper.getPaperEndTime());
        dto.setStudentClass(studentClass);

        if (paper.getPaperPattern() != null) {
            dto.setPaperPatternId(paper.getPaperPattern().getPaperPatternId());
        }

        // Convert questions -> DTOs without answers
        List<QuestionNoAnswerDTO> questionDTOs = paper.getPaperQuestions().stream()
                .map(PaperQuestion::getQuestion) // PaperQuestion -> Question
                .map(this::convertToQuestionNoAnswerDTO) // Question -> QuestionNoAnswerDTO
                .collect(Collectors.toList());

        // Shuffle for mobile
        Collections.shuffle(questionDTOs);

        dto.setQuestions(questionDTOs);

        return dto;
    }



//    @Override
//    public PaperWithQuestionsDTOn startExam(Integer userId, Integer paperId, String studentClass) {
//        User user = userRepository.findById(Long.valueOf(userId))
//                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
//
//        Paper paper = paperRepository.findById(paperId)
//                .orElseThrow(() -> new ResourceNotFoundException("Paper not found with ID: " + paperId));
//
//        LocalDateTime now = LocalDateTime.now();
//
//        // Check for existing session
//        Optional<ExamSession> existingSessionOpt = examSessionRepository
//                .findByUserIdAndPaperId(userId, paperId);
//
//        if (existingSessionOpt.isPresent()) {
//            ExamSession existingSession = existingSessionOpt.get();
//            if (paper.getEndTime() != null && now.isAfter(paper.getEndTime())) {
//                throw new ExamTimeWindowException("Exam time is over. You cannot resume the exam.");
//            }
//
//            // Return existing session details
//            PaperWithQuestionsDTOn dto = new PaperWithQuestionsDTOn();
//            dto.setSessionId(existingSession.getSessionId());
//            dto.setPaperId(paper.getPaperId());
//            dto.setTitle(paper.getTitle());
//            dto.setDescription(paper.getDescription());
//            dto.setStartTime(paper.getStartTime());
//            dto.setEndTime(paper.getEndTime());
//            dto.setIsLive(paper.getIsLive());
//            dto.setStudentClass(existingSession.getStudentClass());
//            if (paper.getPaperPattern() != null) {
//                dto.setPaperPatternId(paper.getPaperPattern().getPaperPatternId());
//            }
//
//            List<QuestionNoAnswerDTO> questionDTOs = paper.getPaperQuestions().stream()
//                    .map(PaperQuestion::getQuestion)
//                    .map(this::convertToQuestionNoAnswerDTO)
//                    .collect(Collectors.toList());
//            Collections.shuffle(questionDTOs);
//            dto.setQuestions(questionDTOs);
//            return dto;
//        }
//
//
//
//
//        if (paper.getStartTime() != null && paper.getEndTime() != null) {
//            if (now.isBefore(paper.getStartTime()) || now.isAfter(paper.getEndTime())) {
//                throw new ExamTimeWindowException("Exam can only be started between " + paper.getStartTime() + " and " + paper.getEndTime());
//            }
//        }
//
//        ExamSession session = new ExamSession();
//        session.setUser(user);
//        session.setPaper(paper);
//        session.setStudentClass(studentClass);
//        session.setStartTime(now);
//        session.setScore(0.0);
//        session.setUserAnswers(new ArrayList<>());
//        ExamSession savedSession = examSessionRepository.save(session);
//
//        // Schedule result processing if paper has an end time
//        if (paper.getEndTime() != null) {
//            // Schedule result processing for 5 minutes after the paper end time
//            LocalDateTime resultDateTime = paper.getEndTime().plusMinutes(5);
//            examSessionSchedulingService.scheduleExamResultProcessing(savedSession.getSessionId(), resultDateTime);
//        }
//
//        PaperWithQuestionsDTOn dto = new PaperWithQuestionsDTOn();
//        dto.setSessionId(savedSession.getSessionId());
//        dto.setPaperId(paper.getPaperId());
//        dto.setTitle(paper.getTitle());
//        dto.setDescription(paper.getDescription());
//        dto.setStartTime(paper.getStartTime());
//        dto.setEndTime(paper.getEndTime());
//        dto.setIsLive(paper.getIsLive());
//        dto.setStudentClass(studentClass);
//        dto.setPaperPatternId(paper.getPaperPattern().getPaperPatternId());
//
//        List<QuestionNoAnswerDTO> questionDTOs = paper.getPaperQuestions().stream()
//                .map(PaperQuestion::getQuestion)
//                .map(this::convertToQuestionNoAnswerDTO)
//                .collect(Collectors.toList());
//        Collections.shuffle(questionDTOs);
//        dto.setQuestions(questionDTOs);
//        return dto;
//    }


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
//            for (UserAnswerDTO dto : answers) {
//                Question question = questionRepository.findById(dto.getQuestionId())
//                        .orElseThrow(() -> new ResourceNotFoundException("Question not found with ID: " + dto.getQuestionId()));
//
//                String selectedAns = dto.getSelectedOption();
//                String correctAns = question.getAnswer();
//
//                boolean isMultiOptions = question.isMultiOptions();
//                double questionMarks = question.getMarks();
//
//                if (question.isDescriptive()) {
//                    // Save descriptive answer
//                    DescriptiveAns da = new DescriptiveAns();
//                    da.setQuestionId(question.getQuestionId());
//                    da.setPaperId(paper.getPaperId());
//                    da.setUserId(userId.intValue());
//                    da.setAns(selectedAns);
//                    descriptiveAnsRepository.save(da);
//
//                    // Check correctness and apply marking logic
//                    if (correctAns != null && selectedAns != null) {
//                        boolean isCorrect = correctAns.trim().equalsIgnoreCase(selectedAns.trim());
//
//                        if (isCorrect) {
//                            score += questionMarks;
//                        } else {
//                            negativeCount++;
//
//                            Optional<NegativeMarks> negative = negativeMarksRepository.findByPaperAndQuestionId(paper, question.getQuestionId());
//
//                            double questionNegative = negative.map(NegativeMarks::getNegativeMark)
//                                    .orElse(questionMarks * negativePerWrong);
//
//                            negativeScore += questionNegative;
//                        }
//                    }
//
//                } else {
//                    // Save objective answer
//                    UserAnswer ua = new UserAnswer();
//                    ua.setExamSession(session);
//                    ua.setQuestion(question);
//                    ua.setSelectedOption(selectedAns);
//                    userAnswers.add(ua);
//
//                    if (correctAns != null && selectedAns != null && isMultiOptions) {
//                        Set<Character> correctSet = correctAns.chars().mapToObj(c -> (char) c).collect(Collectors.toSet());
//                        Set<Character> selectedSet = selectedAns.chars().mapToObj(c -> (char) c).collect(Collectors.toSet());
//
//                        if (selectedSet.equals(correctSet)) {
//                            score += questionMarks;
//                        } else if (correctSet.containsAll(selectedSet)) {
//                            score += questionMarks / 2.0;
//                        } else {
//                            negativeScore += 2.0;
//                            negativeCount++;
//                        }
//
//                    } else {
//                        boolean isCorrect = correctAns != null && correctAns.equalsIgnoreCase(selectedAns);
//
//                        if (isCorrect) {
//                            score += questionMarks;
//                        } else {
//                            negativeCount++;
//
//                            Optional<NegativeMarks> negative = negativeMarksRepository.findByPaperAndQuestionId(paper, question.getQuestionId());
//
//                            double questionNegative = negative.map(NegativeMarks::getNegativeMark)
//                                    .orElse(questionMarks * negativePerWrong);
//
//                            negativeScore += questionNegative;
//                        }
//                    }
//                }
//            }
//
//            double finalScore = score - negativeScore;
//            session.setEndTime(LocalDateTime.now());
//            session.setScore((double) Math.round(finalScore));
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
//

    private double getNegativeMarks(Paper paper, Integer questionId, double questionMarks, double negativePerWrong) {
        return negativeMarksRepository.findByPaperAndQuestionId(paper, questionId)
                .map(NegativeMarks::getNegativeMark)
                .orElse(questionMarks * negativePerWrong);
    }

    @Override
    @Transactional
    public ResponseDto1<Double> submitExamAnswers(Integer sessionId, Long userId, List<UserAnswerDTO> answers) {
        try {
            // Fetch session, throw if not found
            ExamSession session = examSessionRepository.findById(sessionId)
                    .orElseThrow(() -> new ResourceNotFoundException("Exam session not found with ID: " + sessionId));

            // Prevent double submission
            if (session.getScore() != null) {
                return ResponseDto1.error("Exam submission error", "Exam already submitted for this session.");
            }

            // Verify user ownership (log warning but continue)
            if (!session.getUser().getId().equals(userId)) {
                System.out.println("Warning: User ID " + userId + " is submitting for session owned by user " + session.getUser().getId());
            }

            Paper paper = session.getPaper();
            List<Question> allQuestions = paper.getPaperQuestions().stream()
                    .map(PaperQuestion::getQuestion)
                    .collect(Collectors.toList());
            int totalQuestions = allQuestions.size();

            // ===== Handle EMPTY answers array: set all scoring fields to zero =====
            if (answers == null || answers.isEmpty()) {
                session.setEndTime(LocalDateTime.now());
                session.setScore(0.0);
                session.setRightAnswers(0);
                session.setWrongAnswers(0);
                session.setAttemptedQuestions(0);
                session.setTotalQuestions(totalQuestions);
                session.setNegativeCount(0.0);
                session.setNegativeScore(0.0);
                session.setUserAnswers(new ArrayList<>());
                session.setResultDate(paper.getResultDate());

                examSessionRepository.save(session);

                return ResponseDto1.success(
                        "Exam submitted successfully (empty answers)",
                        paper.getPaperId(),
                        session.getStartTime().toLocalDate(),
                        session.getEndTime().toLocalDate()
                );
            }

            // Get negative marking info
            int negativeType = Optional.ofNullable(paper.getPaperPattern())
                    .map(PaperPattern::getNegativeMarks)
                    .orElse(0);
            double negativePerWrong = switch (negativeType) {
                case 1 -> 0.25;
                case 2 -> 0.50;
                case 3 -> 0.75;
                case 4 -> 1.0;
                default -> 0.0;
            };

            // Map questionId -> UserAnswerDTO for O(1) lookup
            Map<Integer, UserAnswerDTO> answerMap = answers.stream()
                    .collect(Collectors.toMap(UserAnswerDTO::getQuestionId, a -> a));

            List<UserAnswer> userAnswers = new ArrayList<>();
            double score = 0;
            double negativeScore = 0;
            int negativeCount = 0;
            int right = 0;
            int wrong = 0;

            for (Question question : allQuestions) {
                UserAnswerDTO dto = answerMap.get(question.getQuestionId());
                String selectedAns = (dto != null) ? dto.getSelectedOption() : null;
                String correctAns = question.getAnswer();
                boolean isMultiOptions = question.isMultiOptions();
                double marks = question.getMarks();
                boolean attempted = selectedAns != null && !selectedAns.trim().isEmpty();
                boolean isCorrect = false;

                if (question.isDescriptive()) {
                    // Save descriptive answers
                    DescriptiveAns da = new DescriptiveAns();
                    da.setQuestionId(question.getQuestionId());
                    da.setPaperId(paper.getPaperId());
                    da.setUserId(userId.intValue());
                    da.setAns(selectedAns);
                    descriptiveAnsRepository.save(da);

                    if (attempted && correctAns != null) {
                        isCorrect = correctAns.trim().equalsIgnoreCase(selectedAns.trim());
                        if (isCorrect) {
                            score += marks;
                        } else {
                            wrong++;
                            negativeCount++;
                            negativeScore += getNegativeMarks(paper, question.getQuestionId(), marks, negativePerWrong);
                        }
                    }
                    // Unattempted descriptive questions do not count as right or wrong
                } else {
                    // Objective questions
                    UserAnswer ua = new UserAnswer();
                    ua.setExamSession(session);
                    ua.setQuestion(question);
                    ua.setSelectedOption(selectedAns);
                    userAnswers.add(ua);

                    if (attempted) {
                        if (isMultiOptions && correctAns != null) {
                            Set<Character> correctSet = correctAns.chars().mapToObj(c -> (char)c).collect(Collectors.toSet());
                            Set<Character> selectedSet = selectedAns.chars().mapToObj(c -> (char)c).collect(Collectors.toSet());

                            if (selectedSet.equals(correctSet)) {
                                isCorrect = true;
                                score += marks;
                            } else if (correctSet.containsAll(selectedSet)) {
                                score += marks / 2.0;
                                // partial credit, do not count as wrong
                            } else {
                                wrong++;
                                negativeCount++;
                                negativeScore += getNegativeMarks(paper, question.getQuestionId(), marks, negativePerWrong);
                            }
                        } else {
                            // Single choice objective
                            if (correctAns != null && correctAns.equalsIgnoreCase(selectedAns)) {
                                isCorrect = true;
                                score += marks;
                            } else {
                                wrong++;
                                negativeCount++;
                                negativeScore += getNegativeMarks(paper, question.getQuestionId(), marks, negativePerWrong);
                            }
                        }
                    }
                }

                if (attempted && isCorrect) right++;
                // wrong incremented during scoring for objective questions above
            }

            int attempted = right + wrong;
            int unsolved = totalQuestions - attempted;
            double finalScore = score - negativeScore;

            // Save session results
            session.setEndTime(LocalDateTime.now());
            session.setScore(Math.round(finalScore * 100.0) / 100.0);  // rounded to 2 decimals
            session.setUserAnswers(userAnswers);
            session.setNegativeCount((double) negativeCount);
            session.setNegativeScore(negativeScore);
            session.setResultDate(paper.getResultDate());
            session.setRightAnswers(right);
            session.setWrongAnswers(wrong);
            session.setAttemptedQuestions(attempted);
            session.setTotalQuestions(totalQuestions);

            examSessionRepository.save(session);

            return ResponseDto1.success(
                    "Exam submitted successfully",
                    paper.getPaperId(),
                    session.getStartTime().toLocalDate(),
                    session.getEndTime().toLocalDate()
            );

        } catch (Exception e) {
            return ResponseDto1.error("Failed to submit exam", e.getMessage());
        }
    }

//    @Override
//    @Transactional
//    public ResponseDto1<Double> submitExamAnswers(Integer sessionId, Long userId, List<UserAnswerDTO> answers) {
//
//        try {
//            ExamSession session = examSessionRepository.findById(sessionId)
//                    .orElseThrow(() -> new ResourceNotFoundException("Exam session not found with ID: " + sessionId));
//
//            // === Prevent resubmission if score is already set ===
//            if (session.getScore() != null) {
//                return ResponseDto1.error("Exam submission error", "Exam already submitted for this session.");
//            }
//
//            if (!session.getUser().getId().equals(userId)) {
//                System.out.println("Warning: User ID " + userId + " is submitting for session owned by user " + session.getUser().getId());
//            }
//
//            double score = 0;
//            double negativeScore = 0.0;
//            int negativeCount = 0;
//            int right = 0;
//            int wrong = 0;
//
//            // Map questionId -> answerDTO for fast lookup
//            Map<Integer, UserAnswerDTO> answerMap = answers.stream()
//                    .collect(Collectors.toMap(UserAnswerDTO::getQuestionId, a -> a));
//
//            // Get the paper and all questions from it
//            Paper paper = session.getPaper();
//            List<Question> allQuestions = paper.getPaperQuestions().stream()
//                    .map(PaperQuestion::getQuestion)
//                    .collect(Collectors.toList());
//            int totalQuestions = allQuestions.size();
//
//            Integer patternId = paper.getPaperPattern() != null
//                    ? paper.getPaperPattern().getPaperPatternId()
//                    : null;
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
//            List<UserAnswer> userAnswers = new ArrayList<>();
//
//            for (Question question : allQuestions) {
//                UserAnswerDTO dto = answerMap.get(question.getQuestionId());
//                String selectedAns = (dto != null) ? dto.getSelectedOption() : null;
//                String correctAns = question.getAnswer();
//                boolean isMultiOptions = question.isMultiOptions();
//                double questionMarks = question.getMarks();
//                boolean isCorrect = false;
//                boolean isAttempted = false;
//
//                // Attempted flag for attempted calculation
//                if (selectedAns != null && !selectedAns.trim().isEmpty()) {
//                    isAttempted = true;
//                }
//
//                if (question.isDescriptive()) {
//                    // Save descriptive answer
//                    DescriptiveAns da = new DescriptiveAns();
//                    da.setQuestionId(question.getQuestionId());
//                    da.setPaperId(paper.getPaperId());
//                    da.setUserId(userId.intValue());
//                    da.setAns(selectedAns);
//                    descriptiveAnsRepository.save(da);
//
//                    // Evaluate descriptive if possible (if correctAns is set)
//                    if (correctAns != null && selectedAns != null && !selectedAns.trim().isEmpty()) {
//                        isAttempted = true;
//                        if (correctAns.trim().equalsIgnoreCase(selectedAns.trim())) {
//                            isCorrect = true;
//                            score += questionMarks; // Descriptive correct
//                        } else {
//                            wrong++;
//                            negativeCount++;
//                            negativeScore += getNegativeMarks(paper, question.getQuestionId(), questionMarks, negativePerWrong);
//                        }
//                    }
//                    // If descriptive is not attempted or no correct answer, count as unsolved
//                } else {
//                    // Save UserAnswer for objective type
//                    UserAnswer ua = new UserAnswer();
//                    ua.setExamSession(session);
//                    ua.setQuestion(question);
//                    ua.setSelectedOption(selectedAns);
//                    userAnswers.add(ua);
//
//                    if (selectedAns != null && !selectedAns.trim().isEmpty()) {
//                        if (isMultiOptions) {
//                            Set<Character> correctSet = correctAns != null
//                                    ? correctAns.chars().mapToObj(c -> (char) c).collect(Collectors.toSet())
//                                    : Collections.emptySet();
//                            Set<Character> selectedSet = selectedAns.chars().mapToObj(c -> (char) c).collect(Collectors.toSet());
//
//                            if (selectedSet.equals(correctSet)) {
//                                isCorrect = true;
//                                score += questionMarks;
//                            } else if (correctSet.containsAll(selectedSet)) {
//                                score += questionMarks / 2.0;
//                            } else {
//                                wrong++;
//                                negativeCount++;
//                                negativeScore += getNegativeMarks(paper, question.getQuestionId(), questionMarks, negativePerWrong);
//                            }
//                        } else { // Single choice
//                            if (correctAns != null && correctAns.equalsIgnoreCase(selectedAns)) {
//                                isCorrect = true;
//                                score += questionMarks;
//                            } else {
//                                wrong++;
//                                negativeCount++;
//                                negativeScore += getNegativeMarks(paper, question.getQuestionId(), questionMarks, negativePerWrong);
//                            }
//                        }
//                    }
//                }
//
//                if (isAttempted && isCorrect) {
//                    right++;
//                } else if (isAttempted && !isCorrect && !question.isDescriptive()) {
//                    // wrong already incremented above for obj type
//                }
//                // For unattempted, do nothing here (will calculate at end)
//            }
//
//            int attempted = right + wrong;
//            int unsolved = totalQuestions - attempted;
//
//            double finalScore = score - negativeScore;
//
//            session.setEndTime(LocalDateTime.now());
//            session.setScore((double) Math.round(finalScore));
//            session.setUserAnswers(userAnswers);
//            session.setNegativeCount((double) negativeCount);
//            session.setNegativeScore(negativeScore);
//            session.setResultDate(paper.getResultDate());
//
//            session.setRightAnswers(right);
//            session.setWrongAnswers(wrong);
//            session.setAttemptedQuestions(attempted);
//            session.setTotalQuestions(totalQuestions);
//
//            examSessionRepository.save(session);
//
//            // You may return the number of unsolved (unattempted)
//            // Or to include in response DTO if desired
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
    public List<ExamPaperSummaryDto> getUniquePaperSummaries() {
        List<Integer> paperIds = examSessionRepository.findDistinctPaperIds();
        List<Paper> papers = paperRepository.findAllById(paperIds);

        List<ExamPaperSummaryDto> dtos = new ArrayList<>();
        for (Paper paper : papers) {
            // Fetch all ExamSessions for this paper
            List<ExamSession> sessions = examSessionRepository.findByPaper_PaperId(paper.getPaperId());

            // Get startTime and resultDate from the first session, or use aggregation logic


            dtos.add(new ExamPaperSummaryDto(
                    paper.getPaperId(),
                    paper.getTitle(),
                    paper.getStartTime(),
                    paper.getResultDate()

            ));
        }
        return dtos;
    }

    @Override
    public List<ExamSessionShowResultDto> getSessionsByPaperId(Integer paperId) {

        List<ExamSession> sessions = examSessionRepository.findByPaper_PaperId(paperId);
        if (sessions == null || sessions.isEmpty()) {
            throw new ResourceNotFoundException("No ExamSession found for paperId: " + paperId);
        }

        // Get the paper object once, since all sessions have the same paper
        Paper paper = paperRepository.findById(paperId)
                .orElseThrow(() -> new ResourceNotFoundException("Paper not found for paperId: " + paperId));
        String paperTitle = paper.getTitle();

        List<ExamSessionShowResultDto> result = new ArrayList<>();
        for (ExamSession session : sessions) {
            User user = session.getUser();
            String studentName = "";
            if (user != null) {
                studentName = (user.getFirstName() != null ? user.getFirstName() : "") +
                        " " +
                        (user.getLastName() != null ? user.getLastName() : "");
                studentName = studentName.trim();
            }

            result.add(new ExamSessionShowResultDto(
                    session.getSessionId(),
                    user != null ? user.getId() : null,
                    studentName,
                    paper.getPaperId(),
                    paperTitle,
                    session.getStudentClass(),
                    session.getStartTime(),
                    session.getEndTime(),
                    session.getResultDate(),
                    session.getScore(),
                    session.getNegativeCount(),
                    session.getNegativeScore(),
                    session.getRightAnswers(),         // ✅ added
                    session.getWrongAnswers(),         // ✅ added
                    session.getAttemptedQuestions(),   // ✅ added
                    session.getTotalQuestions()
            ));
        }
        return result;
    }
}
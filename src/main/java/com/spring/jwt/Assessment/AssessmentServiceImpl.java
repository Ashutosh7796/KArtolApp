package com.spring.jwt.Assessment;
import com.spring.jwt.Question.QuestionDTO;
import com.spring.jwt.Question.QuestionDtoWithoutAns;
import com.spring.jwt.entity.Assessment;
import com.spring.jwt.entity.Question;
import com.spring.jwt.exception.UserNotFoundExceptions;
import com.spring.jwt.repository.UserRepository;
import com.spring.jwt.Question.QuestionRepository;
import com.spring.jwt.Question.QuestionNotFoundException;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class AssessmentServiceImpl implements AssessmentService {

//    @Autowired
    private final AssessmentRepository assessmentRepository;

//    @Autowired
    private final QuestionRepository questionRepository;

//    @Autowired
    private final UserRepository userRepository;

    public AssessmentServiceImpl(AssessmentRepository assessmentRepository, QuestionRepository questionRepository, UserRepository userRepository) {
        this.assessmentRepository = assessmentRepository;
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
    }

    // Entity to DTO mapper
    private AssessmentDTO entityToDto(Assessment entity) {
        AssessmentDTO dto = new AssessmentDTO();
        dto.setAssessmentId(entity.getAssessmentId());
        dto.setSetNumber(entity.getSetNumber());
        dto.setAssessmentDate(entity.getAssessmentDate());
        dto.setDuration(entity.getDuration());
        dto.setStartTime(entity.getStartTime());
        dto.setEndTime(entity.getEndTime());
        dto.setUserId(entity.getUser() != null ? entity.getUser().getId() : null);

        // Map question IDs
        List<Question> questionEntities = entity.getQuestions() != null ? entity.getQuestions() : Collections.emptyList();
        dto.setQuestionIds(questionEntities.stream().map(Question::getQuestionId).collect(Collectors.toList()));

        // Map full questions
        List<QuestionDTO> questionDtos = questionEntities.stream().map(q -> {
            QuestionDTO qDto = new QuestionDTO();
            qDto.setQuestionId(q.getQuestionId());
            qDto.setQuestionText(q.getQuestionText());
            qDto.setType(q.getType());
            qDto.setSubject(q.getSubject());
            qDto.setLevel(q.getLevel());
            qDto.setMarks(q.getMarks());
//            qDto.setUserId(q.getUserId());
            qDto.setOption1(q.getOption1());
            qDto.setOption2(q.getOption2());
            qDto.setOption3(q.getOption3());
            qDto.setOption4(q.getOption4());
            qDto.setAnswer(q.getAnswer());
            return qDto;
        }).collect(Collectors.toList());
        dto.setQuestions(questionDtos);

        return dto;
    }
    private AssessmentDtoWithoutAns entityToDtoWithoutAns(Assessment entity) {
        AssessmentDtoWithoutAns dto = new AssessmentDtoWithoutAns();
        dto.setAssessmentId(entity.getAssessmentId());
        dto.setSetNumber(entity.getSetNumber());
        dto.setAssessmentDate(entity.getAssessmentDate());
        dto.setDuration(entity.getDuration());
        dto.setStartTime(entity.getStartTime());
        dto.setEndTime(entity.getEndTime());
        dto.setUserId(entity.getUser() != null ? entity.getUser().getId() : null);

        // Map question IDs
        List<Question> questionEntities = entity.getQuestions() != null ? entity.getQuestions() : Collections.emptyList();
        dto.setQuestionIds(questionEntities.stream().map(Question::getQuestionId).collect(Collectors.toList()));

        // Map full questions
        List<QuestionDtoWithoutAns> questionDtos = questionEntities.stream().map(q -> {
            QuestionDtoWithoutAns qDto = new QuestionDtoWithoutAns();
            qDto.setQuestionId(q.getQuestionId());
            qDto.setQuestionText(q.getQuestionText());
            qDto.setType(q.getType());
            qDto.setSubject(q.getSubject());
            qDto.setLevel(q.getLevel());
            qDto.setMarks(q.getMarks());
//            qDto.setUserId(q.getUserId());
            qDto.setOption1(q.getOption1());
            qDto.setOption2(q.getOption2());
            qDto.setOption3(q.getOption3());
            qDto.setOption4(q.getOption4());
            return qDto;
        }).collect(Collectors.toList());
        dto.setQuestions(questionDtos);

        return dto;
    }
    // DTO to Entity mapper
    private Assessment dtoToEntity(AssessmentDTO dto) {
        Assessment entity = new Assessment();
        entity.setAssessmentId(dto.getAssessmentId());
        entity.setSetNumber(dto.getSetNumber());
        entity.setAssessmentDate(dto.getAssessmentDate());
        entity.setDuration(dto.getDuration());
        entity.setStartTime(dto.getStartTime());
        entity.setEndTime(dto.getEndTime());
        // Set user
        entity.setUser(
                dto.getUserId() != null
                        ? userRepository.findById(dto.getUserId().longValue())
                        .orElseThrow(() -> new UserNotFoundExceptions("UserId not found: " + dto.getUserId()))
                        : null
        );
        // Set question list
        if (dto.getQuestionIds() != null && !dto.getQuestionIds().isEmpty()) {
            List<Question> questions = questionRepository.findAllById(dto.getQuestionIds());
            if (questions.size() != dto.getQuestionIds().size()) {
                Set<Integer> foundIds = questions.stream().map(Question::getQuestionId).collect(Collectors.toSet());
                List<Integer> notFound = dto.getQuestionIds().stream()
                        .filter(id -> !foundIds.contains(id))
                        .collect(Collectors.toList());
                throw new QuestionNotFoundException("QuestionIds not found: " + notFound);
            }
            entity.setQuestions(questions);
        } else {
            entity.setQuestions(Collections.emptyList());
        }
        return entity;
    }

    @Override
    public List<AssessmentDTO> createAssessmentsBulk(AssessmentDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Assessment object is null");
        }

        Integer userId = dto.getUserId();
        if (userId == null || !userRepository.existsById(userId.longValue())) {
            throw new UserNotFoundExceptions("UserId not found in User table: " + userId);
        }

        // Validate question IDs
        Set<Integer> questionIds = dto.getQuestionIds() != null ? new HashSet<>(dto.getQuestionIds()) : new HashSet<>();

        List<Question> foundQuestions = questionRepository.findAllById(questionIds);
        Set<Integer> foundIds = foundQuestions.stream().map(Question::getQuestionId).collect(Collectors.toSet());

        List<Integer> notFound = questionIds.stream()
                .filter(id -> !foundIds.contains(id))
                .collect(Collectors.toList());

        if (!notFound.isEmpty()) {
            throw new QuestionNotFoundException("QuestionIds not found in Question table: " + notFound);
        }

        // Generate unique setNumber
        Long maxSetNumber = assessmentRepository.findMaxSetNumber();
        Long newSetNumber = (maxSetNumber == null ? 1000L : maxSetNumber + 1);

        // Convert to entity and save
        Assessment entity = dtoToEntity(dto);
        entity.setSetNumber(newSetNumber);

        Assessment saved = assessmentRepository.save(entity);

        return Collections.singletonList(entityToDto(saved));
    }
    @Override
    public AssessmentDTO getAssessmentById(Integer id) {
        return assessmentRepository.findById(id)
                .map(this::entityToDto)
                .orElseThrow(() -> new AssessmentNotFoundException("Assessment not found with id: " + id));
    }
    @Override
    public AssessmentDtoWithoutAns getAssessmentByIdWithoutAns(Integer id) {
        return assessmentRepository.findById(id)
                .map(this::entityToDtoWithoutAns)
                .orElseThrow(() -> new AssessmentNotFoundException("Assessment not found with id: " + id));
    }

    @Override
    public List<AssessmentDtoWithoutAns> getAllAssessments() {
        return assessmentRepository.findAll()
                .stream()
                .map(this::entityToDtoWithoutAns)
                .collect(Collectors.toList());
    }

    @Override
    public AssessmentDTO updateAssessment(Integer id, AssessmentDTO dto) {
        Assessment assessment = assessmentRepository.findById(id)
                .orElseThrow(() -> new AssessmentNotFoundException("Assessment not found with id: " + id));
        // Only update mutable fields
        assessment.setAssessmentDate(dto.getAssessmentDate());
        assessment.setDuration(dto.getDuration());
        assessment.setStartTime(dto.getStartTime());
        assessment.setEndTime(dto.getEndTime());
        // Update questions if provided
        if (dto.getQuestionIds() != null && !dto.getQuestionIds().isEmpty()) {
            List<Question> questions = questionRepository.findAllById(dto.getQuestionIds());
            if (questions.size() != dto.getQuestionIds().size()) {
                Set<Integer> foundIds = questions.stream().map(Question::getQuestionId).collect(Collectors.toSet());
                List<Integer> notFound = dto.getQuestionIds().stream()
                        .filter(qid -> !foundIds.contains(qid))
                        .collect(Collectors.toList());
                throw new QuestionNotFoundException("QuestionIds not found: " + notFound);
            }
            assessment.setQuestions(questions);
        }
        Assessment saved = assessmentRepository.save(assessment);
        return entityToDto(saved);
    }

    @Override
    public void deleteAssessment(Integer id) {
        if (!assessmentRepository.existsById(id)) {
            throw new AssessmentNotFoundException("Assessment not found with id: " + id);
        }
        assessmentRepository.deleteById(id);
    }
}
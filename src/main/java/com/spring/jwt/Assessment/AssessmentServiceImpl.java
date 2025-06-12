package com.spring.jwt.Assessment;
import com.spring.jwt.entity.Assessment;
import com.spring.jwt.entity.Question;
import com.spring.jwt.exception.UserNotFoundExceptions;
import com.spring.jwt.repository.UserRepository;
import com.spring.jwt.Question.QuestionRepository;
import com.spring.jwt.Question.QuestionNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class AssessmentServiceImpl implements AssessmentService {

    @Autowired
    private AssessmentRepository assessmentRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private UserRepository userRepository;

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
        // Map questions to their IDs
        dto.setQuestionIds(entity.getQuestions() != null ?
                entity.getQuestions().stream().map(Question::getQuestionId).collect(Collectors.toList()) :
                Collections.emptyList());
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
    public List<AssessmentDTO> createAssessmentsBulk(List<AssessmentDTO> dtos) {
        if (dtos == null || dtos.isEmpty()) {
            throw new IllegalArgumentException("Assessment list is empty");
        }

        Integer userId = dtos.get(0).getUserId();
        if (userId == null || !userRepository.existsById(userId.longValue())) {
            throw new UserNotFoundExceptions("UserId not found in User table: " + userId);
        }

        // Gather all question IDs from all DTOs
        Set<Integer> allQuestionIds = dtos.stream()
                .flatMap(dto -> dto.getQuestionIds() != null ? dto.getQuestionIds().stream() : Stream.empty())
                .collect(Collectors.toSet());

        // Check if all questionIds exist in Question table
        List<Question> foundQuestions = questionRepository.findAllById(allQuestionIds);
        Set<Integer> foundIds = foundQuestions.stream().map(Question::getQuestionId).collect(Collectors.toSet());

        List<Integer> notFound = allQuestionIds.stream()
                .filter(id -> !foundIds.contains(id))
                .collect(Collectors.toList());
        if (!notFound.isEmpty()) {
            throw new QuestionNotFoundException("QuestionIds not found in Question table: " + notFound);
        }

        // Generate a unique setNumber (starts at 1000 and increments by 1)
        Long maxSetNumber = assessmentRepository.findMaxSetNumber();
        Long newSetNumber = (maxSetNumber == null ? 1000L : maxSetNumber + 1);

        // Optionally, check for duplicate setNumber with same question set (not required if setNumber always unique)

        // Convert DTOs to entities, assigning same setNumber, user, and questions
        List<Assessment> assessments = dtos.stream()
                .map(dto -> {
                    Assessment entity = dtoToEntity(dto);
                    entity.setSetNumber(newSetNumber);
                    return entity;
                })
                .collect(Collectors.toList());

        List<Assessment> saved = assessmentRepository.saveAll(assessments);
        return saved.stream().map(this::entityToDto).collect(Collectors.toList());
    }

    @Override
    public AssessmentDTO getAssessmentById(Integer id) {
        return assessmentRepository.findById(id)
                .map(this::entityToDto)
                .orElseThrow(() -> new AssessmentNotFoundException("Assessment not found with id: " + id));
    }

    @Override
    public List<AssessmentDTO> getAllAssessments() {
        return assessmentRepository.findAll()
                .stream()
                .map(this::entityToDto)
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
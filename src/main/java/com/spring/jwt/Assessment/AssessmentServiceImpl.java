package com.spring.jwt.Assessment;

import com.spring.jwt.Question.QuestionNotFoundException;
import com.spring.jwt.Question.QuestionRepository;
import com.spring.jwt.entity.Assessment;
import com.spring.jwt.entity.AssessmentQuestion;
import com.spring.jwt.entity.Question;
import com.spring.jwt.entity.User;
import com.spring.jwt.exception.UserNotFoundExceptions;
import com.spring.jwt.repository.UserRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


/////////////////////////////////////////////////////////////////////////////////////
//
//      File Name    : AssessmentService
//      Description  : to perform assessments actions
//      Author       : Ashutosh Shedge
//      Date         : 28/04/2025
//
//////////////////////////////////////////////////////////////////////////////////
@Service
@RequiredArgsConstructor
@Transactional
public class AssessmentServiceImpl implements AssessmentService {

    private final AssessmentRepository assessmentRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final AssessmentMapper assessmentMapper;

    @Override
    public AssessmentDTO createAssessment(AssessmentDTO assessmentDTO) {
        if (assessmentDTO == null) {
            throw new IllegalArgumentException("Assessment data cannot be null");
        }

        if (assessmentDTO.getUserId() == null) {
            throw new IllegalArgumentException("User ID is required");
        }
        User user = userRepository.findById(assessmentDTO.getUserId().longValue())
                .orElseThrow(() -> new UserNotFoundExceptions("User not found with id: " + assessmentDTO.getUserId()));

        Assessment assessment = assessmentMapper.toEntity(assessmentDTO);
        assessment.setUser(user);

        if (assessmentDTO.getQuestionIds() != null && !assessmentDTO.getQuestionIds().isEmpty()) {
            List<Question> questions = questionRepository.findAllById(assessmentDTO.getQuestionIds());

            if (questions.size() != assessmentDTO.getQuestionIds().size()) {
                Set<Integer> foundIds = questions.stream()
                        .map(Question::getQuestionId)
                        .collect(Collectors.toSet());
                
                List<Integer> notFound = assessmentDTO.getQuestionIds().stream()
                        .filter(id -> !foundIds.contains(id))
                        .collect(Collectors.toList());
                
                throw new QuestionNotFoundException("Questions not found with ids: " + notFound);
            }
            
            assessment.setQuestions(questions);
        }

        if (assessment.getSetNumber() == null) {
            Long maxSetNumber = assessmentRepository.findMaxSetNumber();
            Long newSetNumber = (maxSetNumber == null ? 1000L : maxSetNumber + 1);
            assessment.setSetNumber(newSetNumber);
        }

        Assessment saved = assessmentRepository.save(assessment);

        return assessmentMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public AssessmentDTO getAssessmentById(Integer id) {
        return assessmentRepository.findById(id)
                .map(assessmentMapper::toDto)
                .orElseThrow(() -> new AssessmentNotFoundException("Assessment not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AssessmentDTO> getAllAssessments(Pageable pageable) {
        return assessmentRepository.findAll(pageable)
                .map(assessmentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssessmentDTO> getAllAssessments() {
        return assessmentRepository.findAll().stream()
                .map(assessmentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public AssessmentDTO updateAssessment(Integer id, AssessmentDTO assessmentDTO) {
        Assessment assessment = assessmentRepository.findById(id)
                .orElseThrow(() -> new AssessmentNotFoundException("Assessment not found with id: " + id));

        if (assessmentDTO.getTitle() != null) {
            assessment.setTitle(assessmentDTO.getTitle());
        }
        if (assessmentDTO.getSubject() != null) {
            assessment.setSubject(assessmentDTO.getSubject());
        }
        if (assessmentDTO.getDescription() != null) {
            assessment.setDescription(assessmentDTO.getDescription());
        }
        if (assessmentDTO.getAssessmentDate() != null) {
            assessment.setAssessmentDate(assessmentDTO.getAssessmentDate());
        }
        if (assessmentDTO.getDuration() != null) {
            assessment.setDuration(assessmentDTO.getDuration());
        }
        if (assessmentDTO.getStartTime() != null) {
            assessment.setStartTime(assessmentDTO.getStartTime());
        }
        if (assessmentDTO.getEndTime() != null) {
            assessment.setEndTime(assessmentDTO.getEndTime());
        }
        if (assessmentDTO.getIsActive() != null) {
            assessment.setIsActive(assessmentDTO.getIsActive());
        }

        if (assessmentDTO.getQuestionIds() != null && !assessmentDTO.getQuestionIds().isEmpty()) {
            List<Question> questions = questionRepository.findAllById(assessmentDTO.getQuestionIds());

            if (questions.size() != assessmentDTO.getQuestionIds().size()) {
                Set<Integer> foundIds = questions.stream()
                        .map(Question::getQuestionId)
                        .collect(Collectors.toSet());
                
                List<Integer> notFound = assessmentDTO.getQuestionIds().stream()
                        .filter(qid -> !foundIds.contains(qid))
                        .collect(Collectors.toList());
                
                throw new QuestionNotFoundException("Questions not found with ids: " + notFound);
            }
            
            assessment.setQuestions(questions);
        }

        Assessment saved = assessmentRepository.save(assessment);
        return assessmentMapper.toDto(saved);
    }

    @Override
    public void deleteAssessment(Integer id) {
        Assessment assessment = assessmentRepository.findById(id)
                .orElseThrow(() -> new AssessmentNotFoundException("Assessment not found with id: " + id));
        
        assessmentRepository.delete(assessment);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AssessmentDTO> getAssessmentsByUserId(Integer userId, Pageable pageable) {
        if (!userRepository.existsById(userId.longValue())) {
            throw new UserNotFoundExceptions("User not found with id: " + userId);
        }
        
        return assessmentRepository.findByUserId(userId.longValue(), pageable)
                .map(assessmentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssessmentDTO> getAssessmentsByUserId(Integer userId) {
        if (!userRepository.existsById(userId.longValue())) {
            throw new UserNotFoundExceptions("User not found with id: " + userId);
        }
        
        return assessmentRepository.findByUserId(userId.longValue()).stream()
                .map(assessmentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AssessmentDTO> searchAssessments(Map<String, String> filters, Pageable pageable) {
        Specification<Assessment> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (filters.containsKey("title")) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("title")), 
                    "%" + filters.get("title").toLowerCase() + "%"
                ));
            }
            
            if (filters.containsKey("subject")) {
                predicates.add(criteriaBuilder.equal(
                    criteriaBuilder.lower(root.get("subject")), 
                    filters.get("subject").toLowerCase()
                ));
            }
            
            if (filters.containsKey("createdBy")) {
                predicates.add(criteriaBuilder.equal(
                    root.get("user").get("id"), 
                    Long.parseLong(filters.get("createdBy"))
                ));
            }
            
            if (filters.containsKey("isActive")) {
                predicates.add(criteriaBuilder.equal(
                    root.get("isActive"), 
                    Boolean.parseBoolean(filters.get("isActive"))
                ));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        
        return assessmentRepository.findAll(spec, pageable)
                .map(assessmentMapper::toDto);
    }

    @Override
    public AssessmentDTO addQuestionToAssessment(Integer assessmentId, AssessmentDTO.AssessmentQuestionDTO questionDTO) {

        Assessment assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new AssessmentNotFoundException("Assessment not found with id: " + assessmentId));

        Question question = questionRepository.findById(questionDTO.getQuestionId())
                .orElseThrow(() -> new QuestionNotFoundException("Question not found with id: " + questionDTO.getQuestionId()));

        boolean questionExists = assessment.getQuestions().stream()
                .anyMatch(q -> q.getQuestionId().equals(questionDTO.getQuestionId()));
        
        if (questionExists) {
            throw new DuplicateQuestionInSetException("Question already exists in the assessment: " + questionDTO.getQuestionId());
        }

        assessment.getQuestions().add(question);

        if (questionDTO.getPoints() != null || questionDTO.getOrderNumber() != null) {
            AssessmentQuestion assessmentQuestion = new AssessmentQuestion();
            assessmentQuestion.setAssessment(assessment);
            assessmentQuestion.setQuestion(question);
            
            if (questionDTO.getPoints() != null) {
                assessmentQuestion.setPoints(questionDTO.getPoints());
            }
            
            if (questionDTO.getOrderNumber() != null) {
                assessmentQuestion.setOrderNumber(questionDTO.getOrderNumber());
            }

        }
        
        Assessment savedAssessment = assessmentRepository.save(assessment);
        return assessmentMapper.toDto(savedAssessment);
    }

    @Override
    public AssessmentDTO removeQuestionFromAssessment(Integer assessmentId, Integer questionId) {
        Assessment assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new AssessmentNotFoundException("Assessment not found with id: " + assessmentId));

        boolean questionExists = assessment.getQuestions().removeIf(q -> q.getQuestionId().equals(questionId));
        
        if (!questionExists) {
            throw new QuestionNotFoundException("Question not found in the assessment: " + questionId);
        }

        Assessment savedAssessment = assessmentRepository.save(assessment);
        return assessmentMapper.toDto(savedAssessment);
    }
}
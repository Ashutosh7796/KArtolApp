package com.spring.jwt.Question;

import com.spring.jwt.entity.Question;
import com.spring.jwt.entity.enum01.QType;
import com.spring.jwt.exception.PageNotFoundException;
import com.spring.jwt.exception.ResourceNotFoundException;
import com.spring.jwt.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Validated
@Slf4j
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final QuestionMapper questionMapper;

    @Override
    @Transactional
    public QuestionDTO createQuestion(@Valid QuestionDTO questionDTO) {
        log.debug("Creating question: {}", questionDTO);

        Integer userId = questionDTO.getUserId();
        if (!userRepository.existsById(Long.valueOf(userId))) {
            log.error("User with ID {} does not exist", userId);
            throw new InvalidQuestionException("User with userId " + userId + " does not exist.");
        }

        Question question = questionMapper.toEntity(questionDTO);

        Question savedQuestion = questionRepository.save(question);
        log.info("Created question with ID: {}", savedQuestion.getQuestionId());

        return questionMapper.toDto(savedQuestion);
    }

    @Override
    @Transactional(readOnly = true)
    public QuestionDTO getQuestionById(Integer id) {
        log.debug("Getting question by ID: {}", id);
        
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Question with ID {} not found", id);
                    return new QuestionNotFoundException("Question not found with id: " + id);
                });
                
        return questionMapper.toDto(question);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QuestionDTO> getAllQuestions(Pageable pageable) {
        log.debug("Getting only soft-deleted questions with pagination: {}", pageable);

        Page<Question> questionPage = questionRepository.findByDeletedTrue(pageable);
        return questionPage.map(questionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QuestionDTO> getAllQuestionsDeleted(Pageable pageable) {
        log.debug("Getting only soft-deleted questions with pagination: {}", pageable);

        Page<Question> questionPage = questionRepository.findByDeletedFalse(pageable);
        return questionPage.map(questionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionDTO> getAllQuestions() {
        log.debug("Getting all questions without pagination");
        
        List<Question> questions = questionRepository.findAll();
        return questionMapper.toDtoList(questions);
    }

    @Override
    @Transactional
    public QuestionDTO updateQuestion(Integer id, QuestionUpdateDTO questionUpdateDTO) {
        log.debug("Updating question with ID: {}", id);

        Question question = questionRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Question with ID {} not found for update", id);
                    return new QuestionNotFoundException("Question not found with id: " + id);
                });

        questionMapper.updateEntityFromDto(questionUpdateDTO, question);

        Question updatedQuestion = questionRepository.save(question);
        log.info("Updated question with ID: {}", updatedQuestion.getQuestionId());

        return questionMapper.toDto(updatedQuestion);
    }


    @Override
    @Transactional
    public void deleteQuestion(Integer id) {
        log.debug("Deleting question with ID: {}", id);
        
        if (!questionRepository.existsById(id)) {
            log.error("Question with ID {} not found for deletion", id);
            throw new QuestionNotFoundException("Question not found with id: " + id);
        }
        
        questionRepository.deleteById(id);
        log.info("Deleted question with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QuestionDTO> getQuestionsByUserId(Integer userId, Pageable pageable) {
        log.debug("Getting questions by user ID: {} with pagination: {}", userId, pageable);
        
        Page<Question> questionPage = questionRepository.findByUserId(userId, pageable);
        if (questionPage.isEmpty()) {
            log.warn("No questions found for user ID: {}", userId);
        }
        
        return questionPage.map(questionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QuestionDTO> getQuestionsByUserIdAndDescriptive(Integer userId, boolean isDescriptive, Pageable pageable) {
        log.debug("Getting questions by user ID: {} with isDescriptive: {} and pagination: {}", userId, isDescriptive, pageable);

        Page<Question> questionPage = questionRepository.findByUserIdAndIsDescriptive(userId, isDescriptive, pageable);

        if (questionPage.isEmpty()) {
            log.warn("No questions found for user ID: {} with isDescriptive: {}", userId, isDescriptive);
            throw new ResourceNotFoundException("No questions found for user ID: " + userId);
        }

        // Check if requested page is out of bounds
        if (pageable.getPageNumber() >= questionPage.getTotalPages()) {
            log.warn("Requested page number {} exceeds total pages {}", pageable.getPageNumber(), questionPage.getTotalPages());
            throw new PageNotFoundException("Requested page " + pageable.getPageNumber() + " exceeds total available pages " + questionPage.getTotalPages());
        }

        return questionPage.map(questionMapper::toDto);
    }



    @Override
    @Transactional(readOnly = true)
    public List<QuestionDTO> getQuestionsByUserId(Integer userId) {
        log.debug("Getting questions by user ID: {} without pagination", userId);
        
        List<Question> questions = questionRepository.findByUserId(userId);
        if (questions.isEmpty()) {
            log.warn("No questions found for user ID: {}", userId);
            throw new QuestionNotFoundException("No questions found for userId: " + userId);
        }
        
        return questionMapper.toDtoList(questions);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<QuestionDTO> searchQuestions(Map<String, String> filters, Pageable pageable) {
        log.debug("Searching questions with filters: {} and pagination: {}", filters, pageable);
        
        Specification<Question> spec = buildSpecificationFromFilters(filters);
        Page<Question> questionPage = questionRepository.findAll(spec, pageable);
        
        return questionPage.map(questionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionDTO> getQuestionsBySubTypeLevelMarks(String subject, QType type, String level, String marks) {
        log.debug("Getting questions by subject: {}, type: {}, level: {}, marks: {}", subject, type, level, marks);
        
        if (subject == null && type == null && level == null && marks == null) {
            log.error("At least one filter field must be provided");
            throw new InvalidQuestionException("At least one filter field (subject, type, level, marks) must be provided.");
        }
        
        Map<String, String> filters = new HashMap<>();
        if (StringUtils.hasText(subject)) filters.put("subject", subject);
        if (StringUtils.hasText(String.valueOf(type))) filters.put("type", String.valueOf(type));
        if (StringUtils.hasText(level)) filters.put("level", level);
        if (StringUtils.hasText(marks)) filters.put("marks", marks);
        
        Specification<Question> spec = buildSpecificationFromFilters(filters);
        List<Question> questions = questionRepository.findAll(spec);
        
        if (questions.isEmpty()) {
            log.warn("No questions found for the given criteria");
            throw new QuestionNotFoundException("No questions found for the given criteria.");
        }
        
        return questionMapper.toDtoList(questions);
    }
    
    /**
     * Build a JPA Specification from a map of filters
     */
    private Specification<Question> buildSpecificationFromFilters(Map<String, String> filters) {
        Specification<Question> spec = Specification.where(null);
        
        for (Map.Entry<String, String> entry : filters.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            
            if (StringUtils.hasText(value)) {
                spec = spec.and((root, query, cb) -> cb.equal(root.get(key), value));
            }
        }
        return spec;
    }

    @Override
    @Transactional
    public List<QuestionDTO> createQuestionsBulk(@Valid BulkQuestionDTO bulkDTO) {
        log.debug("Bulk creating questions for subject: {}, userId: {}, class: {}",
                bulkDTO.getSubject(), bulkDTO.getUserId(), bulkDTO.getStudentClass());

        // Check if user exists
        if (!userRepository.existsById(Long.valueOf(bulkDTO.getUserId()))) {
            log.error("User with ID {} does not exist", bulkDTO.getUserId());
            throw new InvalidQuestionException("User with userId " + bulkDTO.getUserId() + " does not exist.");
        }

        // Check if questions list is empty
        if (bulkDTO.getQuestions() == null || bulkDTO.getQuestions().isEmpty()) {
            log.error("No questions provided in bulk request");
            throw new InvalidQuestionException("At least one question is required.");
        }

        // Validate each question for essential fields (example: questionText, type, level, option1, option2, answer)
        for (SingleQuestionDTO single : bulkDTO.getQuestions()) {
            if (isBlank(single.getQuestionText()) ||
                    isBlank(String.valueOf(single.getType())) ||
                    isBlank(single.getLevel()) ||
                    isBlank(String.valueOf(single.getMarks()))) {
                log.error("Missing required fields: questionText, type, level, or marks: {}", single);
                throw new InvalidQuestionException("Each question must have questionText, type, level, and marks.");
            }

// Validate options and answer based on descriptive flag
//            isBlank(single.getOption1()) || isBlank(single.getOption2()) ||
            if (!single.isDescriptive()) {
                if ( isBlank(single.getAnswer())) {
                    log.error("MCQ question is missing  or answer: {}", single);
                    throw new InvalidQuestionException("MCQ questions must have  and answer.");
                }
            }
        }

        List<QuestionDTO> createdQuestions = new ArrayList<>();
        for (SingleQuestionDTO single : bulkDTO.getQuestions()) {
            QuestionDTO dto = new QuestionDTO();
            dto.setSubject(bulkDTO.getSubject());
            dto.setUserId(bulkDTO.getUserId());
            dto.setStudentClass(bulkDTO.getStudentClass());
            dto.setUnit(bulkDTO.getUnit());
            dto.setChapter(bulkDTO.getChapter());
            dto.setTopic(bulkDTO.getTopic());
            dto.setQuestionText(single.getQuestionText());
            dto.setType(single.getType());
            dto.setLevel(single.getLevel());
            dto.setMarks(single.getMarks());
            dto.setOption1(single.getOption1());
            dto.setOption2(single.getOption2());
            dto.setOption3(single.getOption3());
            dto.setOption4(single.getOption4());
            dto.setAnswer(single.getAnswer());
            dto.setHintAndSol(single.getHintAndSol());
            dto.setDescriptive(single.isDescriptive());
            dto.setMultiOptions(single.isMultiOptions());


            Question question = questionMapper.toEntity(dto);
            Question saved = questionRepository.save(question);
            createdQuestions.add(questionMapper.toDto(saved));
        }
        return createdQuestions;
    }

    // Utility method to check for blank strings
    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    @Override
    @Transactional
    public void updateDeletedStatus(Integer questionId, boolean deleted) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found: " + questionId));
        question.setDeleted(deleted);
        questionRepository.save(question);
    }

    @Override
    @Transactional
    public void toggleDeletedStatus(Integer questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found: " + questionId));
        question.setDeleted(!question.isDeleted());
        questionRepository.save(question);
    }
}
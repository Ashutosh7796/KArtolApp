package com.spring.jwt.Question;

import com.spring.jwt.entity.Question;
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
        log.debug("Getting all questions with pagination: {}", pageable);
        
        Page<Question> questionPage = questionRepository.findAll(pageable);
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
    public QuestionDTO updateQuestion(Integer id, QuestionDTO questionDTO) {
        log.debug("Updating question with ID: {}", id);
        
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Question with ID {} not found for update", id);
                    return new QuestionNotFoundException("Question not found with id: " + id);
                });

        questionMapper.updateEntityFromDto(questionDTO, question);

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
    public List<QuestionDTO> getQuestionsBySubTypeLevelMarks(String subject, String type, String level, String marks) {
        log.debug("Getting questions by subject: {}, type: {}, level: {}, marks: {}", subject, type, level, marks);
        
        if (subject == null && type == null && level == null && marks == null) {
            log.error("At least one filter field must be provided");
            throw new InvalidQuestionException("At least one filter field (subject, type, level, marks) must be provided.");
        }
        
        Map<String, String> filters = new HashMap<>();
        if (StringUtils.hasText(subject)) filters.put("subject", subject);
        if (StringUtils.hasText(type)) filters.put("type", type);
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
}
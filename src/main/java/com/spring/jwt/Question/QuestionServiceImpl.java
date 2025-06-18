package com.spring.jwt.Question;

import com.spring.jwt.entity.Question;
import com.spring.jwt.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.jpa.domain.Specification;
import java.util.List;

@Service
public class QuestionServiceImpl implements QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Question createQuestion(Question question) {
        Integer userId = requireNonNullElseThrow(question.getUserId(), "userId must be provided.");
        if (!userRepository.existsById(Long.valueOf(userId))) {
            throw new InvalidQuestionException("User with userId " + userId + " does not exist.");
        }
        checkHasText(question.getSubject(), "Subject must not be blank.");
        checkHasText(question.getType(), "Type must not be blank.");
        checkHasText(question.getLevel(), "Level must not be blank.");
        checkHasText(question.getMarks(), "Marks must not be blank.");
        checkHasText(question.getQuestionText(), "Question text must not be blank.");

        // Option fields checks (optional)
        checkHasText(question.getOption1(), "Option1 must not be blank.");
        checkHasText(question.getOption2(), "Option2 must not be blank.");
        checkHasText(question.getOption3(), "Option3 must not be blank.");
        checkHasText(question.getOption4(), "Option4 must not be blank.");
        checkHasText(question.getAnswer(), "Answer must not be blank.");

        // Check for duplicate question
        if (questionRepository.existsByQuestionText(question.getQuestionText())) {
            throw new DuplicateQuestionException("Question already added: " + question.getQuestionText());
        }

        return questionRepository.save(question);
    }

    private static Integer requireNonNullElseThrow(Object value, String message) {
        if (value == null) {
            throw new InvalidQuestionException(message);
        }
        if (value instanceof Integer) return (Integer) value;
        if (value instanceof Long) return ((Long) value).intValue();
        if (value instanceof String) return Integer.valueOf((String) value);
        throw new InvalidQuestionException("userId has invalid type.");
    }

    private static void checkHasText(String value, String message) {
        if (!org.springframework.util.StringUtils.hasText(value)) {
            throw new InvalidQuestionException(message);
        }
    }

    @Override
    public Question getQuestionById(Integer id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new QuestionNotFoundException("Question not found with id: " + id));
    }

    @Override
    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    @Override
    public Question updateQuestion(Integer id, Question updatedQuestion) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new QuestionNotFoundException("Question not found with id: " + id));

        setIfNotBlank(updatedQuestion.getQuestionText(), question::setQuestionText);
        setIfNotBlank(updatedQuestion.getType(), question::setType);
        setIfNotBlank(updatedQuestion.getSubject(), question::setSubject);
        setIfNotBlank(updatedQuestion.getLevel(), question::setLevel);
        setIfNotBlank(updatedQuestion.getMarks(), question::setMarks);
        setIfNotBlank(updatedQuestion.getOption1(), question::setOption1);
        setIfNotBlank(updatedQuestion.getOption2(), question::setOption2);
        setIfNotBlank(updatedQuestion.getOption3(), question::setOption3);
        setIfNotBlank(updatedQuestion.getOption4(), question::setOption4);
        setIfNotBlank(updatedQuestion.getAnswer(), question::setAnswer);
        setIfNotBlank(updatedQuestion.getStudentClass(), question::setStudentClass);

        return questionRepository.save(question);
    }

    private void setIfNotBlank(String value, java.util.function.Consumer<String> setter) {
        if (value != null && !value.isBlank()) {
            setter.accept(value);
        }
    }

    @Override
    public void deleteQuestion(Integer id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new QuestionNotFoundException("Question not found with id: " + id));
        questionRepository.delete(question);
    }

    @Override
    public List<Question> getQuestionsByUserId(Integer userId) {
        List<Question> questions = questionRepository.findByUserId(userId);
        if (questions == null || questions.isEmpty()) {
            throw new QuestionNotFoundException("No questions found for userId: " + userId);
        }
        return questions;
    }

    @Override
    public List<Question> getQuestionsBySubTypeLevelMarks(String subject, String type, String level, String marks) {
        if (subject == null && type == null && level == null && marks == null) {
            throw new InvalidQuestionException("At least one filter field (subject, type, level, marks) must be provided.");
        }
        Specification<Question> spec = Specification.where(null);
        if (subject != null) spec = spec.and((root, query, cb) -> cb.equal(root.get("subject"), subject));
        if (type != null)    spec = spec.and((root, query, cb) -> cb.equal(root.get("type"), type));
        if (level != null)   spec = spec.and((root, query, cb) -> cb.equal(root.get("level"), level));
        if (marks != null)   spec = spec.and((root, query, cb) -> cb.equal(root.get("marks"), marks));

        List<Question> questions = questionRepository.findAll(spec);
        if (questions == null || questions.isEmpty()) {
            throw new QuestionNotFoundException("No questions found for the given criteria.");
        }
        return questions;
    }
}
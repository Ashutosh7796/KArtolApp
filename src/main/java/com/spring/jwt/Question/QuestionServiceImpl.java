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
        Long userId = requireNonNullElseThrow(question.getUserId(), "userId must be provided.");
        if (!userRepository.existsById(userId)) {
            throw new InvalidQuestionException("User with userId " + userId + " does not exist.");
        }
        checkHasText(question.getSub(), "Subject (sub) must not be blank.");
        checkHasText(question.getType(), "Type must not be blank.");
        checkHasText(question.getLevel(), "Level must not be blank.");
        checkHasText(question.getMarks(), "Marks must not be blank.");
        checkHasText(question.getQuestion(), "Question must not be blank.");

        return questionRepository.save(question);
    }
    private static Long requireNonNullElseThrow(Object value, String message) {
        if (value == null) {
            throw new InvalidQuestionException(message);
        }
        if (value instanceof Long) return (Long) value;
        if (value instanceof Integer) return ((Integer) value).longValue();
        if (value instanceof String) return Long.valueOf((String) value);
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

        setIfNotBlank(updatedQuestion.getQuestion(), question::setQuestion);
        setIfNotBlank(updatedQuestion.getType(), question::setType);
        setIfNotBlank(updatedQuestion.getSub(), question::setSub);
        setIfNotBlank(updatedQuestion.getLevel(), question::setLevel);
        setIfNotBlank(updatedQuestion.getMarks(), question::setMarks);
        setIfNotNull(updatedQuestion.getOp1(), question::setOp1);
        setIfNotNull(updatedQuestion.getOp2(), question::setOp2);
        setIfNotNull(updatedQuestion.getOp3(), question::setOp3);
        setIfNotNull(updatedQuestion.getOp4(), question::setOp4);
        setIfNotNull(updatedQuestion.getAns(), question::setAns);
        setIfNotNull(updatedQuestion.getQuestioncol(), question::setQuestioncol);

        return questionRepository.save(question);
    }
    private void setIfNotBlank(String value, java.util.function.Consumer<String> setter) {
        if (value != null && !value.isBlank()) {
            setter.accept(value);
        }
    }
    private <T> void setIfNotNull(T value, java.util.function.Consumer<T> setter) {
        if (value != null) {
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
    public List<Question> getQuestionsBySubTypeLevelMarks(String sub, String type, String level, String marks) {
        if (sub == null && type == null && level == null && marks == null) {
            throw new InvalidQuestionException("At least one filter field (sub, type, level, marks) must be provided.");
        }
        Specification<Question> spec = Specification.where(null);
        if (sub != null)   spec = spec.and((root, query, cb) -> cb.equal(root.get("sub"), sub));
        if (type != null)  spec = spec.and((root, query, cb) -> cb.equal(root.get("type"), type));
        if (level != null) spec = spec.and((root, query, cb) -> cb.equal(root.get("level"), level));
        if (marks != null) spec = spec.and((root, query, cb) -> cb.equal(root.get("marks"), marks));

        List<Question> questions = questionRepository.findAll(spec);
        if (questions == null || questions.isEmpty()) {
            throw new QuestionNotFoundException("No questions found for the given criteria.");
        }
        return questions;
    }
}
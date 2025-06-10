package com.spring.jwt.Question;


import com.spring.jwt.entity.Question;

import java.util.List;

public interface QuestionService {
    Question createQuestion(Question question);
    Question getQuestionById(Integer id);
    List<Question> getAllQuestions();
    Question updateQuestion(Integer id, Question question);
    void deleteQuestion(Integer id);
    List<Question> getQuestionsByUserId(Integer userId);
    List<Question> getQuestionsBySubTypeLevelMarks(String sub, String type, String level, String marks);
}
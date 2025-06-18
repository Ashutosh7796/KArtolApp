package com.spring.jwt.Exam.controller;

import com.spring.jwt.Exam.Dto.*;
import com.spring.jwt.Exam.service.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exam")
public class ExamController {

    @Autowired
    private ExamService examService;

    @PostMapping("/start")
    public ExamSessionDTO startExam(
            @RequestParam Integer userId,
            @RequestParam Integer paperId,
            @RequestParam String studentClass) {
        return examService.startExam(userId, paperId, studentClass);
    }

    // Submit answers for session
    @PostMapping("/{sessionId}/submit")
    public ExamSessionDTO submitExam(
            @PathVariable Integer sessionId,
            @RequestParam Long userId,
            @RequestBody List<UserAnswerDTO> answers) {
        return examService.submitExamAnswers(sessionId, userId, answers);
    }

    @GetMapping("/results/{userId}")
    public List<ExamResultDTO> getExamResultsByUser(@PathVariable Long userId) {
        return examService.getResultsByUserId(userId);
    }

    @GetMapping("/session/{sessionId}/qa")
    public List<SessionQuestionAnswerDTO> getSessionQuestionsAndAnswers(@PathVariable Integer sessionId) {
        return examService.getQuestionsAndAnswersBySessionId(sessionId);
    }

    @GetMapping("/results/student-classes")
    public List<StudentClassResultDTO> getAllStudentClassResults() {
        return examService.getResultsGroupedByStudentClass();
    }
}
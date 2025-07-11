package com.spring.jwt.Exam.controller;

import com.spring.jwt.Exam.Dto.*;
import com.spring.jwt.Exam.service.ExamService;
import com.spring.jwt.dto.ResponseDto;
import com.spring.jwt.exception.ResourceNotFoundException;
import com.spring.jwt.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/exam")
public class ExamController {

    @Autowired
    private ExamService examService;

    @PostMapping("/start")
    public PaperWithQuestionsDTOn startExam(
            @RequestParam Integer userId,
            @RequestParam Integer paperId,
            @RequestParam String studentClass) {
        return examService.startExam(userId, paperId, studentClass);
    }

    // Submit answers for session
    @PostMapping("/{sessionId}/submit")
    public ResponseDto<ResponseDto1<Double>> submitExam(
            @PathVariable Integer sessionId,
            @RequestParam Long userId,
            @RequestBody List<UserAnswerDTO> answers) {
        try {
            ResponseDto1<Double> dto = examService.submitExamAnswers(sessionId, userId, answers);
            return ResponseDto.success("Exam submitted successfully", dto);
        } catch (ResourceNotFoundException ex) {
            return ResponseDto.error("Exam submission failed: Resource not found", ex.getMessage());
        } catch (Exception ex) {
            return ResponseDto.error("An unexpected error occurred during exam submission", ex.getMessage());
        }
    }

    @GetMapping("/results/{userId}")
    public List<ExamResultDTO> getExamResultsByUser(@PathVariable Long userId) {
        return examService.getResultsByUserId(userId);
    }

    @GetMapping("/session/{sessionId}/qa")
    public List<SessionQuestionAnswerDTO> getSessionQuestionsAndAnswers(@PathVariable Integer sessionId) {
        return examService.getQuestionsAndAnswersBySessionId(sessionId);
    }

    @GetMapping("/studentClasses")
    public List<StudentClassResultDTO> getAllStudentClassResults() {
        return examService.getResultsGroupedByStudentClass();
    }

    @GetMapping("/sessions")
    public ResponseEntity<List<ExamSessionDTO>> getAllExamSessions() {
        List<ExamSessionDTO> sessions = examService.getAllExamSessions();
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/session/last/{userId}")
    public ResponseEntity<ExamSessionDTO> getLastSessionByUserId(@PathVariable Long userId) {
        ExamSessionDTO session = examService.getLastExamSessionByUserId(userId);
        return ResponseEntity.ok(session);
    }


    @GetMapping("/unique-papers")
    public ResponseEntity<ResponseDto<List<ExamPaperSummaryDto>>> getAllUniqueExamPapers() {
        try {
            List<ExamPaperSummaryDto> papers = examService.getAllUniquePapers();
            return ResponseEntity.ok(new ResponseDto<>("Unique exam papers fetched successfully", papers, null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    new ResponseDto<>("Failed to fetch unique exam papers", null, e.getMessage())
            );
        }
    }

}
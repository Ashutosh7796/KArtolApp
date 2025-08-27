package com.spring.jwt.Exam.service;

import com.spring.jwt.Exam.Dto.*;
import com.spring.jwt.Exam.entity.ExamSession;
import com.spring.jwt.dto.ResponseDto;

import java.util.List;

public interface ExamService {
//    ExamSessionDTO startExam(Integer userId, Integer paperId, String studentClass);
PaperWithQuestionsDTOn startExam(Integer userId, Integer paperId, String studentClass);

//    public ExamSessionDTO submitExamAnswers(Integer sessionId, Long userId, List<UserAnswerDTO> answers);

    ResponseDto1<Double> submitExamAnswers(Integer sessionId, Long userId, List<UserAnswerDTO> answers);

    List<ExamResultDTO> getResultsByUserId(Long userId);

    List<SessionQuestionAnswerDTO> getQuestionsAndAnswersBySessionId(Integer sessionId);

    List<StudentClassResultDTO> getResultsGroupedByStudentClass();

    List<ExamSessionDTO> getAllExamSessions();

    ExamSessionDTO getLastExamSessionByUserId(Long userId);

    public List<ExamPaperSummaryDto> getUniquePaperSummaries();

    List<ExamSessionShowResultDto> getSessionsByPaperId(Integer paperId);

    PaperWithQuestionsDTOnn startExamMobile(Integer userId, Integer paperId, String studentClass);
}
package com.spring.jwt.Exam.service;

import com.spring.jwt.Exam.Dto.PaperDTO;
import com.spring.jwt.Exam.Dto.PaperDTO1;
import com.spring.jwt.Exam.Dto.PaperWithQuestionsAndAnswersDTO;
import com.spring.jwt.Exam.Dto.PaperWithQuestionsDTO;
import com.spring.jwt.dto.PageResponseDto;

import java.util.List;

public interface PaperService {
    PaperDTO createPaper(PaperDTO paperDTO);
    PaperDTO1 getPaper(Integer id);
    public PageResponseDto<PaperDTO> getAllPapers(int page, int size);
    PaperDTO updatePaper(Integer id, PaperDTO paperDTO);
    void deletePaper(Integer id);
    //  method for your requirement
    PaperWithQuestionsDTO getPaperWithQuestions(Integer paperId);
    List<PaperDTO> getLivePapers(String studentClass);

    /**
     * Returns a paper along with its questions, answers, and hints.
     *
     * @param paperId the ID of the paper to fetch
     * @return PaperWithQuestionsAndAnswersDTO including all questions with answers and solutions
     */
    PaperWithQuestionsAndAnswersDTO getPaperWithSolutions(Integer paperId);




}
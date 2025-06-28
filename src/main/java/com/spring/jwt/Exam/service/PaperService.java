package com.spring.jwt.Exam.service;

import com.spring.jwt.Exam.Dto.PaperDTO;
import com.spring.jwt.Exam.Dto.PaperWithQuestionsDTO;
import com.spring.jwt.dto.PageResponseDto;

import java.util.List;

public interface PaperService {
    PaperDTO createPaper(PaperDTO paperDTO);
    PaperDTO getPaper(Integer id);
    public PageResponseDto<PaperDTO> getAllPapers(int page, int size);
    PaperDTO updatePaper(Integer id, PaperDTO paperDTO);
    void deletePaper(Integer id);
    //  method for your requirement
    PaperWithQuestionsDTO getPaperWithQuestions(Integer paperId);
    List<PaperDTO> getLivePapers(String studentClass);




}
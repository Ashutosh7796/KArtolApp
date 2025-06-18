package com.spring.jwt.Exam.service;

import com.spring.jwt.Exam.Dto.PaperDTO;
import com.spring.jwt.Exam.Dto.PaperWithQuestionsDTO;
import java.util.List;

public interface PaperService {
    PaperDTO createPaper(PaperDTO paperDTO);
    PaperDTO getPaper(Integer id);
    List<PaperDTO> getAllPapers();
    PaperDTO updatePaper(Integer id, PaperDTO paperDTO);
    void deletePaper(Integer id);
    //  method for your requirement
    PaperWithQuestionsDTO getPaperWithQuestions(Integer paperId);
}
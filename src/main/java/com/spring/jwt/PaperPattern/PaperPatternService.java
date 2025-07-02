package com.spring.jwt.PaperPattern;


import java.util.List;
public interface PaperPatternService {
    PaperPatternDto createPaperPattern(PaperPatternDto paperPatternDto);
    PaperPatternDto getPaperPatternById(Integer id);
    List<PaperPatternDto>getAllPaperPatterns();
    PaperPatternDto updatePaperPattern(Integer id,PaperPatternDto paperPatternDto);
    void deletePaperPattern(Integer id);
}
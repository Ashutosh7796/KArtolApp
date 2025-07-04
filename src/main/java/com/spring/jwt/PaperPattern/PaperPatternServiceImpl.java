package com.spring.jwt.PaperPattern;

import com.spring.jwt.entity.PaperPattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
@Service
@Transactional
public
class PaperPatternServiceImpl implements PaperPatternService {
    @Autowired    private PaperPatternRepository paperPatternRepository;
    @Autowired    private PatternMapper mapper;
    @Override
    public PaperPatternDto createPaperPattern(PaperPatternDto paperPatternDto) {
        if (paperPatternDto == null) {
            throw new IllegalArgumentException("paper pattern data cannot be null");
        }        PaperPattern entity = mapper.toEntity(paperPatternDto);
        PaperPattern savedPattern = paperPatternRepository.save(entity);
        PaperPatternDto dto = mapper.toDto(savedPattern);
        return dto;    }
    @Override
    @Transactional(readOnly = true)
    public PaperPatternDto getPaperPatternById(Integer id) {
        return paperPatternRepository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new PaperPatternNotFoundException("paper pattern not found with id :" + id));
    }
    @Override
    @Transactional(readOnly = true)
    public List<PaperPatternDto> getAllPaperPatterns() {
        return paperPatternRepository.findAll()
                .stream().map(mapper::toDto)
                .collect(Collectors.toList());
    }    @Override
    public PaperPatternDto updatePaperPattern(Integer id, PaperPatternDto paperPatternDto) {
        PaperPattern paperPattern = paperPatternRepository.findById(id)
                .orElseThrow(() -> new PaperPatternNotFoundException("Pattern not found with id: " + id));
        if (paperPatternDto.getPatternName() != null) {
            paperPattern.setPatternName(paperPatternDto.getPatternName());
        }        if (paperPatternDto.getType() != null) {
            paperPattern.setType(paperPatternDto.getType());
        }        if (paperPatternDto.getSubject() != null) {
            paperPattern.setSubject(paperPatternDto.getSubject());
        }        if (paperPatternDto.getNoOfQuestion() != null) {
            paperPattern.setNoOfQuestion(paperPatternDto.getNoOfQuestion());
        }        if (paperPatternDto.getRequiredQuestion() != null) {
            paperPattern.setRequiredQuestion(paperPatternDto.getRequiredQuestion());
        }        if (paperPatternDto.getMarks() != null) {
            paperPattern.setMarks(paperPatternDto.getMarks());
        }        if (paperPatternDto.getNegativeMarks() != null) {
            paperPattern.setNegativeMarks(paperPatternDto.getNegativeMarks());
        }            PaperPattern savedPattern = paperPatternRepository.save(paperPattern);
        return mapper.toDto(savedPattern);
    }    @Override
    public void deletePaperPattern(Integer id) {
        PaperPattern paperPattern = paperPatternRepository.findById(id)
                .orElseThrow(() -> new PaperPatternNotFoundException("paper pattern not found"));
        paperPatternRepository.delete(paperPattern);
    }
}
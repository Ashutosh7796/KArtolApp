package com.spring.jwt.PaperPattern;

import com.spring.jwt.entity.PaperPattern;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
@Component
@RequiredArgsConstructor
public
class PatternMapper {
    public PaperPatternDto toDto(PaperPattern paperPattern){
        if(paperPattern==null){
            return null;
        }        PaperPatternDto dto = new PaperPatternDto();
        dto.setPaperPatternId(paperPattern.getPaperPatternId());
        dto.setType(paperPattern.getType());
        dto.setMarks(paperPattern.getMarks());
        dto.setPatternName(paperPattern.getPatternName());
        dto.setSubject(paperPattern.getSubject());
        dto.setNoOfQuestion(paperPattern.getNoOfQuestion());
        dto.setRequiredQuestion(paperPattern.getRequiredQuestion());
        dto.setNegativeMarks(paperPattern.getNegativeMarks());
        dto.setMCQ(paperPattern.getMCQ());
        dto.setDESCRIPTIVE(paperPattern.getDESCRIPTIVE());
        return dto;
    }    public PaperPattern toEntity(PaperPatternDto dto){
        if(dto==null){
            return null;
        }        PaperPattern paperPattern = new PaperPattern();
        if(dto.getPaperPatternId()!=null){
            paperPattern.setPaperPatternId(dto.getPaperPatternId());
        }        paperPattern.setPatternName(dto.getPatternName());
        paperPattern.setSubject(dto.getSubject());
        paperPattern.setType(dto.getType());
        paperPattern.setNoOfQuestion(dto.getNoOfQuestion());
        paperPattern.setRequiredQuestion(dto.getRequiredQuestion());
        paperPattern.setMarks(dto.getMarks());
        paperPattern.setNegativeMarks(dto.getNegativeMarks());
        paperPattern.setDESCRIPTIVE(dto.getDESCRIPTIVE());
        paperPattern.setMCQ(dto.getMCQ());
        return paperPattern;    }
}

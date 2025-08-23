package com.spring.jwt.PaperPattern;

import com.spring.jwt.entity.PaperPattern;
import com.spring.jwt.entity.enum01.QType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public
class PaperPatternServiceImpl implements PaperPatternService {
    @Autowired
    private PaperPatternRepository paperPatternRepository;
    @Autowired
    private PatternMapper mapper;

    @Override
    public PaperPatternDto createPaperPattern(PaperPatternDto paperPatternDto) {
        if (paperPatternDto == null) {
            throw new IllegalArgumentException("Paper pattern data cannot be null");
        }

        QType type = paperPatternDto.getType();
        if (type != null) {
            switch (type) {
                case MCQ:
                    if (paperPatternDto.getMCQ() == null || paperPatternDto.getMCQ() <= 0) {
                        throw new IllegalArgumentException("MCQ count must be greater than 0 for type MCQ");
                    }
                    if (paperPatternDto.getDESCRIPTIVE() != null && paperPatternDto.getDESCRIPTIVE() != 0) {
                        throw new IllegalArgumentException("Descriptive count must be 0 for type MCQ");
                    }
                    paperPatternDto.setNoOfQuestion(paperPatternDto.getMCQ());
                    paperPatternDto.setRequiredQuestion(paperPatternDto.getMCQ());
                    break;

                case DESCRIPTIVE:
                    if (paperPatternDto.getDESCRIPTIVE() == null || paperPatternDto.getDESCRIPTIVE() <= 0) {
                        throw new IllegalArgumentException("Descriptive count must be greater than 0 for type DESCRIPTIVE");
                    }
                    if (paperPatternDto.getMCQ() != null && paperPatternDto.getMCQ() != 0) {
                        throw new IllegalArgumentException("MCQ count must be 0 for type DESCRIPTIVE");
                    }
                    paperPatternDto.setNoOfQuestion(paperPatternDto.getDESCRIPTIVE());
                    paperPatternDto.setRequiredQuestion(paperPatternDto.getDESCRIPTIVE());
                    break;

                case MCQ_DESCRIPTIVE:
                    Integer mcq = paperPatternDto.getMCQ();
                    Integer desc = paperPatternDto.getDESCRIPTIVE();

                    if (mcq == null || mcq <= 0 || desc == null || desc <= 0) {
                        throw new IllegalArgumentException("Both MCQ and Descriptive counts must be greater than 0 for type MCQ_DESCRIPTIVE");
                    }

                    int total = mcq + desc;
                    paperPatternDto.setNoOfQuestion(total);
                    paperPatternDto.setRequiredQuestion(total);
                    break;

                default:
                    throw new IllegalArgumentException("Invalid question type: " + type);
            }

        } else {
            throw new IllegalArgumentException("Question type cannot be null");
        }

        PaperPattern entity = mapper.toEntity(paperPatternDto);
        PaperPattern savedPattern = paperPatternRepository.save(entity);
        return mapper.toDto(savedPattern);
    }


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
    }

    @Override
    public PaperPatternDto updatePaperPattern(Integer id, PaperPatternDto paperPatternDto) {
        PaperPattern paperPattern = paperPatternRepository.findById(id)
                .orElseThrow(() -> new PaperPatternNotFoundException("Pattern not found with id: " + id));

        // update normal fields
        Optional.ofNullable(paperPatternDto.getPatternName()).ifPresent(paperPattern::setPatternName);
        Optional.ofNullable(paperPatternDto.getSubject()).ifPresent(paperPattern::setSubject);
        Optional.ofNullable(paperPatternDto.getMarks()).ifPresent(paperPattern::setMarks);
        Optional.ofNullable(paperPatternDto.getNegativeMarks()).ifPresent(paperPattern::setNegativeMarks);

        // handle type-specific logic
        if (paperPatternDto.getType() != null) {
            paperPattern.setType(paperPatternDto.getType());

            switch (paperPatternDto.getType()) {
                case MCQ:
                    if (paperPatternDto.getMCQ() == null || paperPatternDto.getMCQ() <= 0) {
                        throw new IllegalArgumentException("MCQ count is required for type MCQ and must be > 0");
                    }
                    paperPattern.setNoOfQuestion(paperPatternDto.getMCQ());
                    paperPattern.setRequiredQuestion(paperPatternDto.getMCQ());
                    break;

                case DESCRIPTIVE:
                    if (paperPatternDto.getDESCRIPTIVE() == null || paperPatternDto.getDESCRIPTIVE() <= 0) {
                        throw new IllegalArgumentException("Descriptive count is required for type DESCRIPTIVE and must be > 0");
                    }
                    paperPattern.setNoOfQuestion(paperPatternDto.getDESCRIPTIVE());
                    paperPattern.setRequiredQuestion(paperPatternDto.getDESCRIPTIVE());
                    break;

                case MCQ_DESCRIPTIVE:
                    Integer mcq = paperPatternDto.getMCQ();
                    Integer desc = paperPatternDto.getDESCRIPTIVE();
                    if (mcq == null || mcq <= 0 || desc == null || desc <= 0) {
                        throw new IllegalArgumentException("Both MCQ and Descriptive counts are required and must be > 0 for type MCQ_DESCRIPTIVE");
                    }
                    int total = mcq + desc;
                    paperPattern.setNoOfQuestion(total);
                    paperPattern.setRequiredQuestion(total);
                    break;
            }
        }

        PaperPattern savedPattern = paperPatternRepository.save(paperPattern);
        return mapper.toDto(savedPattern);
    }

    @Override
    public void deletePaperPattern(Integer id) {
        PaperPattern paperPattern = paperPatternRepository.findById(id)
                .orElseThrow(() -> new PaperPatternNotFoundException("paper pattern not found"));
        paperPatternRepository.delete(paperPattern);
    }
}
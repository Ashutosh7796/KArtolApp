package com.spring.jwt.Exam.serviceImpl;

import com.spring.jwt.Exam.Dto.*;
import com.spring.jwt.Exam.entity.Paper;
import com.spring.jwt.Exam.entity.PaperQuestion;
import com.spring.jwt.Exam.repository.PaperRepository;
import com.spring.jwt.Exam.service.PaperService;
import com.spring.jwt.Question.QuestionRepository;
import com.spring.jwt.dto.PageResponseDto;
import com.spring.jwt.entity.Question;
import com.spring.jwt.exception.InvalidPaginationParameterException;
import com.spring.jwt.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaperServiceImpl implements PaperService {

    @Autowired
    private PaperRepository paperRepository;

    @Autowired
    private QuestionRepository questionRepository;

    private PaperDTO toDTO(Paper entity) {
        if (entity == null) return null;
        PaperDTO dto = new PaperDTO();
        dto.setPaperId(entity.getPaperId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setStartTime(entity.getStartTime());
        dto.setEndTime(entity.getEndTime());
        dto.setIsLive(entity.getIsLive());
        dto.setStudentClass(entity.getStudentClass());
        if (entity.getPaperQuestions() != null) {
            dto.setQuestions(
                    entity.getPaperQuestions().stream()
                            .map(pq -> pq.getQuestion() != null ? pq.getQuestion().getQuestionId() : null)
                            .collect(Collectors.toList())
            );
        }
        return dto;
    }

    private Paper toEntity(PaperDTO dto) {
        if (dto == null) return null;
        Paper entity = new Paper();
        entity.setPaperId(dto.getPaperId());
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setStartTime(dto.getStartTime());
        entity.setEndTime(dto.getEndTime());
        entity.setIsLive(dto.getIsLive());
        entity.setStudentClass(dto.getStudentClass());

        if (dto.getQuestions() != null && !dto.getQuestions().isEmpty()) {
            List<PaperQuestion> paperQuestions = dto.getQuestions().stream().map(qId -> {
                PaperQuestion pq = new PaperQuestion();
                pq.setPaper(entity);
                pq.setQuestion(
                        questionRepository.findById(qId)
                                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + qId))
                );
                // Optionally set order or studentClass if you want
                return pq;
            }).collect(Collectors.toList());
            entity.setPaperQuestions(paperQuestions);
        }
        return entity;
    }

    private QuestionDTO toQuestionDTO(PaperQuestion pq) {
        if (pq == null || pq.getQuestion() == null) return null;
        QuestionDTO dto = new QuestionDTO();
        dto.setQuestionId(pq.getQuestion().getQuestionId());
        dto.setQuestionText(pq.getQuestion().getQuestionText());
        dto.setType(pq.getQuestion().getType());
        dto.setSubject(pq.getQuestion().getSubject());
        dto.setLevel(pq.getQuestion().getLevel());
        dto.setMarks(pq.getQuestion().getMarks());
        dto.setUserId(pq.getQuestion().getUserId());
        dto.setOption1(pq.getQuestion().getOption1());
        dto.setOption2(pq.getQuestion().getOption2());
        dto.setOption3(pq.getQuestion().getOption3());
        dto.setOption4(pq.getQuestion().getOption4());
        dto.setStudentClass(pq.getQuestion().getStudentClass());
        dto.setAnswer(pq.getQuestion().getAnswer());
        return dto;
    }
    // Entity to PaperWithQuestionsDTO
    private PaperWithQuestionsDTO toDTO01(Paper entity) {
        if (entity == null) return null;
        PaperWithQuestionsDTO dto = new PaperWithQuestionsDTO();
        dto.setPaperId(entity.getPaperId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setStartTime(entity.getStartTime());
        dto.setEndTime(entity.getEndTime());
        dto.setIsLive(entity.getIsLive());
        dto.setStudentClass(entity.getStudentClass());
        if (entity.getPaperQuestions() != null) {
            dto.setQuestions(entity.getPaperQuestions().stream()
                    .map(pq -> toQuestionNoAnswerDTO(pq.getQuestion()))
                    .collect(Collectors.toList())
            );
        }
        return dto;
    }

    // Helper: Question -> QuestionNoAnswerDTO
    private QuestionNoAnswerDTO toQuestionNoAnswerDTO(Question q) {
        if (q == null) return null;
        QuestionNoAnswerDTO dto = new QuestionNoAnswerDTO();
        dto.setQuestionId(q.getQuestionId());
        dto.setQuestionText(q.getQuestionText());
        dto.setType(q.getType());
        dto.setSubject(q.getSubject());
        dto.setLevel(q.getLevel());
        dto.setMarks(q.getMarks());
        dto.setUserId(q.getUserId());
        dto.setOption1(q.getOption1());
        dto.setOption2(q.getOption2());
        dto.setOption3(q.getOption3());
        dto.setOption4(q.getOption4());
        dto.setStudentClass(q.getStudentClass());
        return dto;
    }


    @Override
    public PaperDTO createPaper(PaperDTO paperDTO) {
        Paper paper = toEntity(paperDTO);
        paper.setPaperId(null);
        Paper saved = paperRepository.save(paper);
        return toDTO(saved);
    }

    @Override
    public PaperDTO getPaper(Integer id) {
        Paper paper = paperRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paper not found with id: " + id));
        return toDTO(paper);
    }

    @Override
    public PageResponseDto<PaperDTO> getAllPapers(int page, int size) {
        try {
            if (page < 0 || size <= 0) {
                throw new IllegalArgumentException("Page number must be >= 0 and size > 0");
            }

            Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
            Page<Paper> paperPage = paperRepository.findAll(pageable);

            List<PaperDTO> paperDTOs = paperPage.getContent().stream()
                    .map(this::toDTO)
                    .collect(Collectors.toList());

            return new PageResponseDto<>(
                    paperDTOs,
                    paperPage.getNumber(),
                    paperPage.getSize(),
                    paperPage.getTotalElements(),
                    paperPage.getTotalPages()
            );
        } catch (IllegalArgumentException e) {
            throw new InvalidPaginationParameterException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch paginated papers", e);
        }
    }



    @Override
    public PaperDTO updatePaper(Integer id, PaperDTO paperDTO) {
        Paper paper = paperRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paper not found with id: " + id));
        paper.setTitle(paperDTO.getTitle());
        paper.setDescription(paperDTO.getDescription());
        paper.setStartTime(paperDTO.getStartTime());
        paper.setEndTime(paperDTO.getEndTime());
        paper.setIsLive(paperDTO.getIsLive());
        paper.setStudentClass(paperDTO.getStudentClass());
        // PaperQuestions update logic can be added if needed
        Paper saved = paperRepository.save(paper);
        return toDTO(saved);
    }

    @Override
    public void deletePaper(Integer id) {
        Paper paper = paperRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paper not found with id: " + id));
        paperRepository.delete(paper);
    }

    @Override
    public PaperWithQuestionsDTO getPaperWithQuestions(Integer paperId) {
        Paper paper = paperRepository.findById(paperId)
                .orElseThrow(() -> new ResourceNotFoundException("Paper not found with id: " + paperId));
        return toDTO01(paper);
    }
}
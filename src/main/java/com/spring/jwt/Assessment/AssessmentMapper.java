package com.spring.jwt.Assessment;

import com.spring.jwt.Question.QuestionRepository;
import com.spring.jwt.entity.Assessment;
import com.spring.jwt.entity.AssessmentQuestion;
import com.spring.jwt.entity.Question;
import com.spring.jwt.entity.enum01.QType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Mapper class to convert between Assessment entities and DTOs
 */
@Component
@RequiredArgsConstructor
public class AssessmentMapper {

    private final QuestionRepository questionRepository;

    /**
     * Convert an Assessment entity to an AssessmentDTO
     */
    public AssessmentDTO toDto(Assessment entity) {
        if (entity == null) {
            return null;
        }
        
        AssessmentDTO dto = new AssessmentDTO();
        dto.setId(entity.getAssessmentId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setDuration(entity.getDuration());
        dto.setSubject(entity.getSubject());
        dto.setTotalMarks(entity.getTotalMarks());
        dto.setPassMarks(entity.getPassMarks());
        dto.setAssessmentDate(entity.getAssessmentDate());
        dto.setStartTime(entity.getStartTime());
        dto.setEndTime(entity.getEndTime());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setIsActive(entity.getIsActive());
        
        if (entity.getUser() != null) {
            dto.setUserId(entity.getUser().getId().intValue());
            dto.setCreatedBy(entity.getUser().getId().intValue());
        }

        if (entity.getQuestions() != null && !entity.getQuestions().isEmpty()) {
            List<AssessmentDTO.AssessmentQuestionDTO> questionDtos = new ArrayList<>();
            for (Question question : entity.getQuestions()) {
                AssessmentDTO.AssessmentQuestionDTO questionDto = new AssessmentDTO.AssessmentQuestionDTO();
                questionDto.setQuestionId(question.getQuestionId());
                questionDto.setAssessmentId(entity.getAssessmentId());
                questionDto.setQuestionText(question.getQuestionText());
                questionDto.setQuestionType(QType.valueOf(String.valueOf(question.getType())));
                questionDto.setQuestionOrder(1);
                questionDto.setMarks(1);
                
                questionDtos.add(questionDto);
            }
            dto.setQuestions(questionDtos);

            dto.setQuestionIds(entity.getQuestions().stream()
                .map(Question::getQuestionId)
                .collect(Collectors.toList()));
        }
        
        return dto;
    }
    
    /**
     * Convert an AssessmentDTO to an Assessment entity
     */
    public Assessment toEntity(AssessmentDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Assessment entity = new Assessment();
        
        if (dto.getId() != null) {
            entity.setAssessmentId(dto.getId());
        }
        
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setDuration(dto.getDuration());
        entity.setSubject(dto.getSubject());
        entity.setTotalMarks(dto.getTotalMarks());
        entity.setPassMarks(dto.getPassMarks());
        entity.setAssessmentDate(dto.getAssessmentDate());
        entity.setStartTime(dto.getStartTime());
        entity.setEndTime(dto.getEndTime());
        entity.setIsActive(dto.getIsActive());

        if (dto.getQuestionIds() != null && !dto.getQuestionIds().isEmpty()) {
            List<Question> questions = questionRepository.findAllById(dto.getQuestionIds());
            entity.setQuestions(questions);
        }
        
        return entity;
    }
    
    /**
     * Update an existing Assessment entity from an AssessmentDTO
     */
    public void updateEntityFromDto(AssessmentDTO dto, Assessment entity) {
        if (dto == null || entity == null) {
            return;
        }
        
        if (dto.getTitle() != null) {
            entity.setTitle(dto.getTitle());
        }
        if (dto.getDescription() != null) {
            entity.setDescription(dto.getDescription());
        }
        if (dto.getDuration() != null) {
            entity.setDuration(dto.getDuration());
        }
        if (dto.getSubject() != null) {
            entity.setSubject(dto.getSubject());
        }
        if (dto.getTotalMarks() != null) {
            entity.setTotalMarks(dto.getTotalMarks());
        }
        if (dto.getPassMarks() != null) {
            entity.setPassMarks(dto.getPassMarks());
        }
        if (dto.getAssessmentDate() != null) {
            entity.setAssessmentDate(dto.getAssessmentDate());
        }
        if (dto.getStartTime() != null) {
            entity.setStartTime(dto.getStartTime());
        }
        if (dto.getEndTime() != null) {
            entity.setEndTime(dto.getEndTime());
        }
        if (dto.getIsActive() != null) {
            entity.setIsActive(dto.getIsActive());
        }
    }
} 
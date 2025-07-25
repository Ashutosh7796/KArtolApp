package com.spring.jwt.Question;

import com.spring.jwt.entity.Question;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper class to convert between Question entities and DTOs
 */
@Component
public class QuestionMapper {

    /**
     * Convert a Question entity to a QuestionDTO
     */
    public QuestionDTO toDto(Question entity) {
        if (entity == null) {
            return null;
        }
        
        return QuestionDTO.builder()
                .questionId(entity.getQuestionId())
                .questionText(entity.getQuestionText())
                .type(entity.getType())
                .subject(entity.getSubject())
                .level(entity.getLevel())
                .marks(entity.getMarks())
                .userId(entity.getUserId())
                .option1(entity.getOption1())
                .option2(entity.getOption2())
                .option3(entity.getOption3())
                .option4(entity.getOption4())
                .answer(entity.getAnswer())
                .topic(entity.getTopic())
                .unit(entity.getUnit())
                .chapter(entity.getChapter())
                .hintAndSol(entity.getHintAndSol())
                .isDescriptive(entity.isDescriptive())
                .isMultiOptions(entity.isMultiOptions())
                .StudentClass(entity.getStudentClass())
                .unit(entity.getUnit())
                .chapter(entity.getChapter())
                .topic(entity.getTopic())

                .build();
    }
    
    /**
     * Convert a QuestionDTO to a Question entity
     */
    public Question toEntity(QuestionDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Question entity = new Question();
        entity.setQuestionId(dto.getQuestionId());
        entity.setQuestionText(dto.getQuestionText());
        entity.setType(dto.getType());
        entity.setSubject(dto.getSubject());
        entity.setLevel(dto.getLevel());
        entity.setMarks(dto.getMarks());
        entity.setUserId(dto.getUserId());
        entity.setOption1(dto.getOption1());
        entity.setOption2(dto.getOption2());
        entity.setOption3(dto.getOption3());
        entity.setOption4(dto.getOption4());
        entity.setAnswer(dto.getAnswer());
        entity.setChapter(dto.getChapter());
        entity.setUnit(dto.getUnit());
        entity.setTopic(dto.getTopic());
        entity.setHintAndSol(dto.getHintAndSol());
        entity.setDescriptive(dto.isDescriptive());
        entity.setMultiOptions(dto.isMultiOptions());
        entity.setStudentClass(dto.getStudentClass());
        entity.setUnit(dto.getUnit());
        entity.setChapter(dto.getChapter());
        entity.setTopic(dto.getTopic());
        entity.setDeleted(true);


        return entity;
    }
    
    /**
     * Update an existing Question entity from a QuestionDTO
     */
//    public void updateEntityFromDto(QuestionDTO dto, Question entity) {
//        if (dto == null || entity == null) {
//            return;
//        }
//
//        if (dto.getQuestionText() != null) {
//            entity.setQuestionText(dto.getQuestionText());
//        }
//        if (dto.getType() != null) {
//            entity.setType(dto.getType());
//        }
//        if (dto.getSubject() != null) {
//            entity.setSubject(dto.getSubject());
//        }
//        if (dto.getLevel() != null) {
//            entity.setLevel(dto.getLevel());
//        }
//        if (dto.getMarks() != null) {
//            entity.setMarks(dto.getMarks());
//        }
//        if (dto.getUserId() != null) {
//            entity.setUserId(dto.getUserId());
//        }
//        if (dto.getOption1() != null) {
//            entity.setOption1(dto.getOption1());
//        }
//        if (dto.getOption2() != null) {
//            entity.setOption2(dto.getOption2());
//        }
//        if (dto.getOption3() != null) {
//            entity.setOption3(dto.getOption3());
//        }
//        if (dto.getOption4() != null) {
//            entity.setOption4(dto.getOption4());
//        }
//        if (dto.getAnswer() != null) {
//            entity.setAnswer(dto.getAnswer());
//        }
//        if (dto.getHintAndSol()!=null){
//            entity.setHintAndSol(dto.getHintAndSol());
//        }
//        if (dto.getUnit() != null) {
//            entity.setUnit(dto.getUnit());
//        }
//        if (dto.getChapter() != null) {
//            entity.setChapter(dto.getChapter());
//        }
//        if (dto.getTopic() != null) {
//            entity.setTopic(dto.getTopic());
//        }
//        if (dto.isMultiOptions() ){
//            entity.setMultiOptions(dto.isMultiOptions());
//        }
//
//    }
    public void updateEntityFromDto(QuestionUpdateDTO dto, Question entity) {
        if (dto == null || entity == null) {
            return;
        }

        if (dto.getQuestionText() != null)        entity.setQuestionText(dto.getQuestionText());
        if (dto.getType() != null)                entity.setType(dto.getType());
        if (dto.getSubject() != null)             entity.setSubject(dto.getSubject());
        if (dto.getUnit() != null)                entity.setUnit(dto.getUnit());
        if (dto.getChapter() != null)             entity.setChapter(dto.getChapter());
        if (dto.getTopic() != null)               entity.setTopic(dto.getTopic());
        if (dto.getLevel() != null)               entity.setLevel(dto.getLevel());
        if (dto.getMarks() != null)               entity.setMarks(dto.getMarks());
        if (dto.getUserId() != null)              entity.setUserId(dto.getUserId());
        if (dto.getOption1() != null)             entity.setOption1(dto.getOption1());
        if (dto.getOption2() != null)             entity.setOption2(dto.getOption2());
        if (dto.getOption3() != null)             entity.setOption3(dto.getOption3());
        if (dto.getOption4() != null)             entity.setOption4(dto.getOption4());
        if (dto.getAnswer() != null)              entity.setAnswer(dto.getAnswer());
        if (dto.getHintAndSol() != null)          entity.setHintAndSol(dto.getHintAndSol());
        if (dto.getStudentClass() != null)        entity.setStudentClass(dto.getStudentClass());
        if (dto.getIsDescriptive() != null)       entity.setDescriptive(dto.getIsDescriptive());
        if (dto.getIsMultiOptions() != null)      entity.setMultiOptions(dto.getIsMultiOptions());
    }


    /**
     * Convert a list of Question entities to a list of QuestionDTOs
     */
    public List<QuestionDTO> toDtoList(List<Question> entities) {
        if (entities == null) {
            return List.of();
        }
        
        return entities.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Convert a list of QuestionDTOs to a list of Question entities
     */
    public List<Question> toEntityList(List<QuestionDTO> dtos) {
        if (dtos == null) {
            return List.of();
        }
        
        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
} 
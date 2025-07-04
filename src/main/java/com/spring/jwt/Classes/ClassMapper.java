package com.spring.jwt.Classes;
import com.spring.jwt.entity.Classes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
@Component
@RequiredArgsConstructor
public class ClassMapper {
    public ClassesDto toDto(Classes classes){
        if(classes==null){
            return null;
        }
        ClassesDto dto = new ClassesDto();
        dto.setClassesId(classes.getClassesId());
        dto.setSub(classes.getSub());
        dto.setStudentClass(classes.getStudentClass());
        dto.setDuration(classes.getDuration());
        dto.setDate(classes.getDate());
        dto.setTeacherId(classes.getTeacherId());
        return dto;
    }    public Classes toEntity(ClassesDto dto){
        if(dto==null){
            return null;
        }
        Classes entity = new Classes();
        entity.setClassesId(dto.getClassesId());
        entity.setSub(dto.getSub());
        entity.setStudentClass(dto.getStudentClass());
        entity.setDuration(dto.getDuration());
        entity.setDate(dto.getDate());
        entity.setTeacherId(dto.getTeacherId());
        return entity;
    }
}
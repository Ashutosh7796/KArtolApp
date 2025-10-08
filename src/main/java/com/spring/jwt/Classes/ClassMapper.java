package com.spring.jwt.Classes;

import com.spring.jwt.entity.Classes;
import com.spring.jwt.entity.Teacher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

@Component
@RequiredArgsConstructor
public class ClassMapper {

    // Convert Entity → Basic DTO
    public ClassesDto toDto(Classes classes) {
        if (classes == null) return null;

        ClassesDto dto = new ClassesDto();
        dto.setClassesId(classes.getClassesId());
        dto.setSub(classes.getSub());
        dto.setStudentClass(classes.getStudentClass());
        dto.setDuration(classes.getDuration());
        dto.setDate(classes.getDate());
        dto.setTeacherId(classes.getTeacherId());
        dto.setTime(classes.getTime() != null ? Time.valueOf(classes.getTime().toLocalTime()) : null);
        return dto;
    }

    // Convert Basic DTO → Entity
    public Classes toEntity(ClassesDto dto) {
        if (dto == null) return null;

        Classes entity = new Classes();
        entity.setClassesId(dto.getClassesId());
        entity.setSub(dto.getSub());
        entity.setStudentClass(dto.getStudentClass());
        entity.setDuration(dto.getDuration());
        entity.setDate(dto.getDate());
        entity.setTeacherId(dto.getTeacherId());
        entity.setTime(dto.getTime() != null ? java.sql.Time.valueOf(dto.getTime().toLocalTime()) : null);
        return entity;
    }

    // Convert Entity + Teacher → Detailed DTO (with teacher name and split date/time)
    public ClassesDetailsDto toDetailsDto(Classes classes, Teacher teacher) {
        if (classes == null) return null;

        ClassesDetailsDto dto = new ClassesDetailsDto();
        dto.setClassesId(classes.getClassesId());
        dto.setSub(classes.getSub());
        dto.setStudentClass(classes.getStudentClass());
        dto.setDuration(classes.getDuration());
        dto.setTeacherId(classes.getTeacherId());
        dto.setDate(classes.getDate());
        dto.setTime(classes.getTime() != null ? classes.getTime().toLocalTime() : null);
        dto.setTeacherName(teacher != null ? teacher.getName() : "Unknown");

        return dto;
    }
}

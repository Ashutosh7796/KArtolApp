package com.spring.jwt.dto;

import com.spring.jwt.entity.Teacher;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherDTO {
    private Integer teacherId;
    private String name;
    private String sub;
    private String deg;
    private String status;
    private Integer userId;

    public static TeacherDTO fromEntity(Teacher teacher) {
        if (teacher == null) {
            return null;
        }

        TeacherDTO dto = new TeacherDTO();
        dto.setTeacherId(teacher.getTeacherId());
        dto.setName(teacher.getName());
        dto.setSub(teacher.getSub());
        dto.setDeg(teacher.getDeg());
        dto.setStatus(teacher.getStatus());
        dto.setUserId(teacher.getUserId());
        
        return dto;
    }
} 
package com.spring.jwt.dto;

import com.spring.jwt.entity.Parents;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParentsDTO {
    private Integer parentsId;
    private String name;
    private String batch;
    private String studentName;
    private Integer studentId;

    public static ParentsDTO fromEntity(Parents parents) {
        if (parents == null) {
            return null;
        }

        ParentsDTO dto = new ParentsDTO();
        dto.setParentsId(parents.getParentsId());
        dto.setName(parents.getName());
        dto.setBatch(parents.getBatch());
        dto.setStudentName(parents.getStudentName());
        dto.setStudentId(parents.getStudentId());
        
        return dto;
    }
} 
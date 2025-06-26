package com.spring.jwt.Notes;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
@Data
public class NotesDTO {
    private Long notesId;
    @NotBlank
    private String studentClass;
    @NotBlank
    private String sub;
    @NotBlank
    private String chapter;
    @NotBlank
    private String topic;
    private String note1;
    private String note2;
    @NotNull
    private Integer teacherId;
    private LocalDate createdDate;
}

package com.spring.jwt.Notes;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotesDto {

    @Schema(description = "Unique id of Notes", example = "1")
    private long notesId;
    @Schema(description = "Class for which these notes are", example = "10th class")
    private String standard;
    @Schema(description = "Subject for which these notes are", example = "Science")
    private String sub;
    @Schema(description = "Notes of particular chapter", example = "Motion")
    private String chapter;
    @Schema(description = "Particular topic notes from chapter", example = "Velocity")
    private String topic;
    @Schema(description = "Note1")
    private String Note1;
    @Schema(description = "Note2")
    private String Note2;
    @Schema(description = "Teachers id for that subject", example = "1")
    private Integer teacherId;
    @Schema(description = "Date on which notes were created",example = "21-06-2025")
    private Date createdDate;
}

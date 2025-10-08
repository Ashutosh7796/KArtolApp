package com.spring.jwt.Classes;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Detailed Classes DTO with Teacher Info and Date-Time")
public class ClassesDetailsDto {

    @Schema(description = "Unique identifier of class", example = "1")
    private long classesId;

    @Schema(description = "Subject name", example = "Mathematics")
    private String sub;

    @Schema(description = "Date on which class will be conducted", example = "21-07-2025")
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate date;

    @Schema(description = "Time at which class will start", example = "10:30")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime time;

    @Schema(description = "Duration of class", example = "1 hr")
    private String duration;

    @Schema(description = "Class of student", example = "10th")
    private String studentClass;

    @Schema(description = "Teacher ID", example = "11")
    private Integer teacherId;

    @Schema(description = "Teacher Name", example = "Mr. Sharma")
    private String teacherName;
}

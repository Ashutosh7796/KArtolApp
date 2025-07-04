
package com.spring.jwt.Classes;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Classes Data Transfer Object")
public class ClassesDto {
    @Schema(description = "unique identifier of classes", example = "1")
    private long classesId;
    @Schema(description = "Name of subject", example = "Chemistry")
    private String sub;
    @Schema(description = "Date on which class will be conducted", example = "21-07-2025")
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate date;
    @Schema(description = "Duration of class", example = "1 hrs")
    private String duration;
    @Schema(description = "Class of student", example = "11th")
    private String studentClass;
    @Schema(description = "Unique id of teacher who will conduct the class", example = "11")
    private Integer teacherId;}
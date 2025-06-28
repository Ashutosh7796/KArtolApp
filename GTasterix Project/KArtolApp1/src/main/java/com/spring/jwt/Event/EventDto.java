package com.spring.jwt.Event;


import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.lang.Integer;
import java.lang.String;
import java.time.LocalDate;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Event Data Transfer Object")
public class EventDto {

    @Schema(description = "unique id of event", example = "1")
    private Integer id;

    @NotBlank(message = "Name is required")
    @Size(min = 10, max = 100, message = "Name must be between 10 and 100 characters")
    @Schema(description = "Name of event", example = "Aws Workshop", required = true)
    private String name;

    @JsonFormat(pattern = "dd-MM-yyyy")
    @Schema(description = "Date at which event will be held", example = "21-06-2025")
    private LocalDate date;

    @NotBlank(message = "Description is required")
    @Size(min = 50, max = 1000, message = "Description must be between 50 to 1000 characters")
    @Schema(description = "Description of the event", example = "Detailed workshop on Aws Cloud")
    private String description;

    @Schema(description = "Type of the Event", example = "workshop")
    private String type;

    @Schema(description = "Date at which event is created", example = "01/06/2025")
    private String createdDate;

    @Schema(description = "Event")
    private String eventcol;

    }



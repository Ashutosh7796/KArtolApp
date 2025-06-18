package com.spring.jwt.CalendarEvent;

import com.spring.jwt.entity.enum01.EventType;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CalendarEventDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private EventType eventType;
    private String colorCode;
    private String examSubject;
    private String examLevel;
    private String examRoom;
    private String examPaperId;
    private Boolean isPublicHoliday;
    private String holidayRegion;
}
package com.spring.jwt.CalendarEvent;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpcomingCalendarResponse {
    private List<CalendarEventDTO> exams;
    private List<CalendarEventDTO> events;
}

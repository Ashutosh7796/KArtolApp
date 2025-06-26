package com.spring.jwt.CalendarEvent;


import java.util.List;

public interface CalendarEventService {
    CalendarEventDTO createEvent(CalendarEventDTO dto);
    CalendarEventDTO getEvent(Long id);
    List<CalendarEventDTO> getAllEvents();
    CalendarEventDTO updateEvent(Long id, CalendarEventDTO dto);
    void deleteEvent(Long id);

    List<CalendarEventDTO> getEventsByMonth(int year, int month);

    UpcomingCalendarResponse getUpcomingEventsAndExams();
}
package com.spring.jwt.CalendarEvent;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/calendar-events")
@Tag(name = "Calendar Events", description = "APIs for managing calendar events like exams and holidays")
public class CalendarEventController {

    @Autowired
    private CalendarEventService calendarEventService;

    @PostMapping
    @Operation(summary = "Create a calendar event", description = "Adds a new event such as an exam, meeting, or holiday to the calendar")
    public CalendarEventDTO create(@RequestBody CalendarEventDTO dto) {
        return calendarEventService.createEvent(dto);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get calendar event by ID", description = "Fetches a specific calendar event using its ID")
    public CalendarEventDTO getById(@PathVariable Long id) {
        return calendarEventService.getEvent(id);
    }

    @GetMapping
    @Operation(summary = "Get all calendar events", description = "Retrieves all calendar events including exams and holidays")
    public List<CalendarEventDTO> getAll() {
        return calendarEventService.getAllEvents();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update calendar event", description = "Updates the details of an existing calendar event using its ID")
    public CalendarEventDTO update(@PathVariable Long id, @RequestBody CalendarEventDTO dto) {
        return calendarEventService.updateEvent(id, dto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete calendar event", description = "Removes a calendar event from the system using its ID")
    public void delete(@PathVariable Long id) {
        calendarEventService.deleteEvent(id);
    }

    @GetMapping("/month")
    @Operation(summary = "Get events by month", description = "Fetches all calendar events for a specific month and year")
    public List<CalendarEventDTO> getEventsByMonth(
            @RequestParam int year,
            @RequestParam int month) {
        return calendarEventService.getEventsByMonth(year, month);
    }

    @GetMapping("/calendar/upcoming")
    @Operation(summary = "Get upcoming events and exams", description = "Retrieves a combined list of upcoming calendar events and exams")
    public ResponseEntity<UpcomingCalendarResponse> getUpcomingEventsAndExams() {
        return ResponseEntity.ok(calendarEventService.getUpcomingEventsAndExams());
    }
}

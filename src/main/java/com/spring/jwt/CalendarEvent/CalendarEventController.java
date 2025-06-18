package com.spring.jwt.CalendarEvent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/calendar-events")
public class CalendarEventController {

    @Autowired
    private CalendarEventService calendarEventService;

    @PostMapping
    public CalendarEventDTO create(@RequestBody CalendarEventDTO dto) {
        return calendarEventService.createEvent(dto);
    }

    @GetMapping("/{id}")
    public CalendarEventDTO getById(@PathVariable Long id) {
        return calendarEventService.getEvent(id);
    }

    @GetMapping
    public List<CalendarEventDTO> getAll() {
        return calendarEventService.getAllEvents();
    }

    @PutMapping("/{id}")
    public CalendarEventDTO update(@PathVariable Long id, @RequestBody CalendarEventDTO dto) {
        return calendarEventService.updateEvent(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        calendarEventService.deleteEvent(id);
    }

    @GetMapping("/month")
    public List<CalendarEventDTO> getEventsByMonth(
            @RequestParam int year,
            @RequestParam int month) {
        return calendarEventService.getEventsByMonth(year, month);
    }
}
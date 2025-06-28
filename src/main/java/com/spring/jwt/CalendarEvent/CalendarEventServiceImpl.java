package com.spring.jwt.CalendarEvent;


import com.spring.jwt.entity.CalendarEvent;
import com.spring.jwt.entity.enum01.EventType;
import com.spring.jwt.exception.ExamOnHolidayException;
import com.spring.jwt.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CalendarEventServiceImpl implements CalendarEventService {

    @Autowired
    private CalendarEventRepository calendarEventRepository;

    private CalendarEventDTO toDTO(CalendarEvent entity) {
        CalendarEventDTO dto = new CalendarEventDTO();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setStartDateTime(entity.getStartDateTime());
        dto.setEndDateTime(entity.getEndDateTime());
        dto.setEventType(entity.getEventType());
        dto.setColorCode(entity.getColorCode());
        dto.setExamSubject(entity.getExamSubject());
        dto.setExamLevel(entity.getExamLevel());
        dto.setExamRoom(entity.getExamRoom());
        dto.setExamPaperId(entity.getExamPaperId());
        dto.setIsPublicHoliday(entity.getIsPublicHoliday());
        dto.setHolidayRegion(entity.getHolidayRegion());
        return dto;
    }

    private CalendarEvent toEntity(CalendarEventDTO dto) {
        return CalendarEvent.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .startDateTime(dto.getStartDateTime())
                .endDateTime(dto.getEndDateTime())
                .eventType(dto.getEventType())
                .colorCode(dto.getColorCode())
                .examSubject(dto.getExamSubject())
                .examLevel(dto.getExamLevel())
                .examRoom(dto.getExamRoom())
                .examPaperId(dto.getExamPaperId())
                .isPublicHoliday(dto.getIsPublicHoliday())
                .holidayRegion(dto.getHolidayRegion())
                .build();
    }

    @Override
    public CalendarEventDTO createEvent(CalendarEventDTO dto) {
        CalendarEvent entity = toEntity(dto);
        entity.setId(null);
        if (entity.getEventType() == EventType.EXAM) {
            List<CalendarEvent> publicHolidays = calendarEventRepository
                    .findByStartDateTimeBetweenAndIsPublicHolidayTrue(
                            entity.getStartDateTime().toLocalDate().atStartOfDay(),
                            entity.getStartDateTime().toLocalDate().atTime(LocalTime.MAX)
                    );

            if (!publicHolidays.isEmpty()) {
                throw new ExamOnHolidayException("Cannot schedule an EXAM on a public holiday.");
            }
        }

        return toDTO(calendarEventRepository.save(entity));
    }



    @Override
    public CalendarEventDTO getEvent(Long id) {
        CalendarEvent entity = calendarEventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Calendar event not found with id: " + id));
        return toDTO(entity);
    }

    @Override
    public List<CalendarEventDTO> getAllEvents() {
        List<CalendarEvent> events = calendarEventRepository.findAll();
        return events.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public CalendarEventDTO updateEvent(Long id, CalendarEventDTO dto) {
        CalendarEvent entity = calendarEventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Calendar event not found with id: " + id));
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setStartDateTime(dto.getStartDateTime());
        entity.setEndDateTime(dto.getEndDateTime());
        entity.setEventType(dto.getEventType());
        entity.setColorCode(dto.getColorCode());
        entity.setExamSubject(dto.getExamSubject());
        entity.setExamLevel(dto.getExamLevel());
        entity.setExamRoom(dto.getExamRoom());
        entity.setExamPaperId(dto.getExamPaperId());
        entity.setIsPublicHoliday(dto.getIsPublicHoliday());
        entity.setHolidayRegion(dto.getHolidayRegion());
        return toDTO(calendarEventRepository.save(entity));
    }

    @Override
    public void deleteEvent(Long id) {
        CalendarEvent entity = calendarEventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Calendar event not found with id: " + id));
        calendarEventRepository.delete(entity);
    }

    @Override
    public List<CalendarEventDTO> getEventsByMonth(int year, int month) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Invalid month value: " + month + ". Month must be between 1 and 12.");
        }
        LocalDateTime monthStart;
        try {
            monthStart = LocalDateTime.of(year, month, 1, 0, 0);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid year or month: " + e.getMessage());
        }
        LocalDateTime monthEnd = monthStart.plusMonths(1).minusSeconds(1);

        List<CalendarEvent> events = calendarEventRepository.findByStartDateTimeBetween(monthStart, monthEnd);

        if (events.isEmpty()) {
            throw new ResourceNotFoundException("No events found for " + year + "-" + String.format("%02d", month));
        }

        return events.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public UpcomingCalendarResponse getUpcomingEventsAndExams() {
        LocalDateTime now = LocalDateTime.now();

        List<CalendarEvent> upcoming = calendarEventRepository.findByStartDateTimeAfter(now);

        List<CalendarEventDTO> exams = upcoming.stream()
                .filter(e -> e.getEventType() == EventType.EXAM)
                .map(this::toDTO)
                .collect(Collectors.toList());

        List<CalendarEventDTO> events = upcoming.stream()
                .filter(e -> e.getEventType() == EventType.EVENT)
                .map(this::toDTO)
                .collect(Collectors.toList());

        return new UpcomingCalendarResponse(exams, events);
    }

}
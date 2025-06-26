package com.spring.jwt.CalendarEvent;


import com.spring.jwt.entity.CalendarEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface CalendarEventRepository extends JpaRepository<CalendarEvent, Long> {


    List<CalendarEvent> findByStartDateTimeBetween(LocalDateTime start, LocalDateTime end);
    List<CalendarEvent> findByStartDateTimeAfter(LocalDateTime dateTime);
    List<CalendarEvent> findByStartDateTimeBetweenAndIsPublicHolidayTrue(
            LocalDateTime startOfDay,
            LocalDateTime endOfDay
    );


}
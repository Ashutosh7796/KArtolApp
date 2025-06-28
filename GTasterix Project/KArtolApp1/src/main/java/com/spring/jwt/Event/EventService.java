package com.spring.jwt.Event;

import com.spring.jwt.entity.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;


public interface EventService {

    EventDto createEvent(EventDto eventDto);
    EventDto getEventById(Integer id);
    List<EventDto> getAllEventsByDate(LocalDate date);
    Page<EventDto> getAllEvents(Pageable pageable);
    List<EventDto>getAllEvents();
    void deleteEvent(Integer id);
    EventDto updateEvent(Integer id, EventDto eventdto);
}

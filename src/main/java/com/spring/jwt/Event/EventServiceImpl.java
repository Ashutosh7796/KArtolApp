package com.spring.jwt.Event;

import com.spring.jwt.entity.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class EventServiceImpl implements EventService{

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventMapper mapper;

    @Override
    public EventDto createEvent(EventDto eventDto) {
        if(eventDto==null){
            throw  new IllegalArgumentException("Event data cannot be null");
        }
        Event entity = mapper.toEntity(eventDto);
        Event savedEvent = eventRepository.save(entity);
        EventDto dto = mapper.toDto(savedEvent);

        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public EventDto getEventById(Integer id) {
        return eventRepository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(()->new EventNotFoundException("Event not found with id :" + id));

    }

    @Override
    @Transactional(readOnly = true)
    public List<EventDto> getAllEventsByDate(LocalDate date) {
        List<EventDto> events = eventRepository.findByDate(date).stream().map(mapper::toDto).collect(Collectors.toList());
        if (events.isEmpty()){
            throw new EventNotFoundException("Events not found");
        }
        return events;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EventDto> getAllEvents(Pageable pageable) {
        return eventRepository.findAll(pageable)
                .map(mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventDto> getAllEvents() {
        return eventRepository.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteEvent(Integer id) {
        Event event = eventRepository.findById(id).orElseThrow(() -> new EventNotFoundException("Event not found with id: " + id));
        eventRepository.delete(event);
    }

    @Override
    public EventDto updateEvent(Integer id, EventDto eventdto) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException("Event not found with id: " + id));

        if (event.getName() != null) {
            event.setName(eventdto.getName());
        }
        if (eventdto.getType() != null) {
            event.setEventType(eventdto.getType());
        }
        if (eventdto.getDescription() != null) {
            event.setDescription(eventdto.getDescription());
        }
        if (eventdto.getCreatedDate() != null) {
            event.setCreatedDate(eventdto.getCreatedDate());
        }
        if (eventdto.getDate() != null) {
            event.setDate(eventdto.getDate());
        }
        if (eventdto.getEventcol() != null) {
            event.setEventCol(eventdto.getEventcol());
        }
        Event savedEvent = eventRepository.save(event);
        return mapper.toDto(savedEvent);
    }
}
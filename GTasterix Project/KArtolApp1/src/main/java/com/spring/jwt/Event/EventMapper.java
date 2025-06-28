package com.spring.jwt.Event;

import com.spring.jwt.entity.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventMapper {

    public EventDto toDto(Event event){
        if(event == null){
            return null;
        }

        EventDto dto = new EventDto();
        dto.setId(event.getEventId());
        dto.setName(event.getName());
        dto.setDescription(event.getDescription());
        dto.setDate(event.getDate());
        dto.setType(event.getEventType());
        dto.setCreatedDate(event.getCreatedDate());

        return dto;

    }

    public Event toEntity(EventDto dto){
        if (dto == null) {
            return null;
        }
        Event event=new Event();

        if(dto.getId()!=null){
            event.setEventId(dto.getId());
        }

        event.setName(dto.getName());
        event.setDescription(dto.getDescription());
        event.setDate(dto.getDate());
        event.setEventType(dto.getType());
        event.setCreatedDate(dto.getCreatedDate());
        event.setEventCol(dto.getEventcol());

        return event;
    }
}

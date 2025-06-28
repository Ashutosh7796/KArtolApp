package com.spring.jwt.Notes;

import com.spring.jwt.entity.Notes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotesMapper {

    public NotesDto toDto(Notes notes){
        if(notes==null){
            return null;
        }
        NotesDto dto = new NotesDto();
        dto.setNotesId(notes.getNotesId());
        dto.setStandard(notes.getStandard());
        dto.setSub(notes.getSub());
        dto.setChapter(notes.getChapter());
        dto.setTopic(notes.getTopic());
        dto.setNote1(notes.getNote1());
        dto.setNote2(notes.getNote2());
        dto.setTeacherId(notes.getTeacherId());
        dto.setCreatedDate(notes.getCreatedDate());

        return dto;
    }

    public Notes toEntity(NotesDto dto){
        if(dto==null){
            return null;
        }

        Notes notes = new Notes();

        notes.setNotesId(dto.getNotesId());
        notes.setStandard(dto.getStandard());
        notes.setSub(dto.getSub());
        notes.setChapter(dto.getChapter());
        notes.setTopic(dto.getTopic());
        notes.setNote1(dto.getNote1());
        notes.setNote2(dto.getNote2());
        notes.setTeacherId(dto.getTeacherId());
        notes.setCreatedDate(dto.getCreatedDate());

        return notes;
        }
    }
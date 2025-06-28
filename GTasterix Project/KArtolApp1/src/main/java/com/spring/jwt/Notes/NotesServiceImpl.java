package com.spring.jwt.Notes;

import com.spring.jwt.entity.Notes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotesServiceImpl implements NotesService{

    @Autowired
    private NotesRepository notesRepository;

    @Autowired
    private NotesMapper notesMapper;


    @Override
    public NotesDto createNotes(NotesDto notesDto) {
        if(notesDto==null) {
            throw new IllegalArgumentException("Notes cannot be null");
        }
            Notes entity = notesMapper.toEntity(notesDto);
            Notes savedNotes = notesRepository.save(entity);
            NotesDto dto = notesMapper.toDto(savedNotes);
            return dto;

    }
}

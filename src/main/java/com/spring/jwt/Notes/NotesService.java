package com.spring.jwt.Notes;

import com.spring.jwt.entity.Notes;

import java.util.List;

public interface NotesService {
    NotesDTO createNote(NotesDTO noteDto);
    NotesDTO getNoteById(Long id);
    List<NotesDTO> getAllNotes();
    NotesDTO patchNote(Long id, NotesDTO partialDto);
    void deleteNote(Long id);

    List<NotesDTO> getNotesByTeacherIdAndClass(Integer teacherId, String studentClass);
    List<NotesDTO> getNotesByStudentClassAndSub(String studentClass, String sub);



}
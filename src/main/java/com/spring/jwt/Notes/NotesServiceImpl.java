package com.spring.jwt.Notes;

import com.spring.jwt.entity.Notes;
import com.spring.jwt.exception.NotesNotCreatedException;
import com.spring.jwt.exception.ResourceNotFoundException;
import com.spring.jwt.exception.TeacherNotFoundException;
import com.spring.jwt.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotesServiceImpl implements NotesService {

    @Autowired
    private NotesRepository notesRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Override
    public NotesDTO createNote(NotesDTO noteDto) {
        validateNoteDTO(noteDto);
        teacherRepository.findById(noteDto.getTeacherId())
                .orElseThrow(() -> new TeacherNotFoundException("Teacher not found with id: " + noteDto.getTeacherId()));
        Notes saved = notesRepository.save(toEntity(noteDto));
        return toDTO(saved);
    }

    @Override
    public NotesDTO getNoteById(Long id) {
        Notes notes = notesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Note not found with id: " + id));
        return toDTO(notes);
    }

    @Override
    public List<NotesDTO> getAllNotes() {
        return notesRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public NotesDTO patchNote(Long id, NotesDTO partialDto) {
        Notes existing = notesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Note not found with id: " + id));

        // Apply non-null fields only (partial update)
        if (partialDto.getStudentClass() != null) {
            existing.setStudentClass(partialDto.getStudentClass());
        }
        if (partialDto.getSub() != null) {
            existing.setSub(partialDto.getSub());
        }
        if (partialDto.getChapter() != null) {
            existing.setChapter(partialDto.getChapter());
        }
        if (partialDto.getTopic() != null) {
            existing.setTopic(partialDto.getTopic());
        }
        if (partialDto.getNote1() != null) {
            existing.setNote1(partialDto.getNote1());
        }
        if (partialDto.getNote2() != null) {
            existing.setNote2(partialDto.getNote2());
        }
        if (partialDto.getTeacherId() != null) {
            existing.setTeacherId(partialDto.getTeacherId());
        }
        if (partialDto.getCreatedDate() != null) {
            existing.setCreatedDate(partialDto.getCreatedDate());
        }

        Notes updated = notesRepository.save(existing);
        return toDTO(updated);
    }


    @Override
    public void deleteNote(Long id) {
        Notes existing = notesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Note not found with id: " + id));
        notesRepository.delete(existing);
    }

    private Notes toEntity(NotesDTO dto) {
        Notes entity = new Notes();
        // Don't set ID here; it will be generated
        entity.setStudentClass(dto.getStudentClass());
        entity.setSub(dto.getSub());
        entity.setChapter(dto.getChapter());
        entity.setTopic(dto.getTopic());
        entity.setNote1(dto.getNote1());
        entity.setNote2(dto.getNote2());
        entity.setTeacherId(dto.getTeacherId());
        entity.setCreatedDate(dto.getCreatedDate());
        return entity;
    }

    private NotesDTO toDTO(Notes entity) {
        NotesDTO dto = new NotesDTO();
        dto.setNotesId(entity.getNotesId());
        dto.setStudentClass(entity.getStudentClass());
        dto.setSub(entity.getSub());
        dto.setChapter(entity.getChapter());
        dto.setTopic(entity.getTopic());
        dto.setNote1(entity.getNote1());
        dto.setNote2(entity.getNote2());
        dto.setTeacherId(entity.getTeacherId());
        dto.setCreatedDate(entity.getCreatedDate());
        return dto;
    }

    private void validateNoteDTO(NotesDTO noteDto) {
        if (noteDto.getTeacherId() == null)
            throw new NotesNotCreatedException("teacherId must not be null.");
        if (noteDto.getStudentClass() == null || noteDto.getStudentClass().trim().isEmpty())
            throw new NotesNotCreatedException("studentClass must not be null or empty.");
        if (noteDto.getSub() == null || noteDto.getSub().trim().isEmpty())
            throw new NotesNotCreatedException("sub must not be null or empty.");
        if (noteDto.getChapter() == null || noteDto.getChapter().trim().isEmpty())
            throw new NotesNotCreatedException("chapter must not be null or empty.");
        if (noteDto.getTopic() == null || noteDto.getTopic().trim().isEmpty())
            throw new NotesNotCreatedException("topic must not be null or empty.");
    }

    @Override
    public List<NotesDTO> getNotesByTeacherIdAndClass(Integer teacherId, String studentClass) {
        List<Notes> notesList = notesRepository.findByTeacherIdAndStudentClass(teacherId, studentClass);
        if (notesList.isEmpty()) {
            throw new ResourceNotFoundException("No notes found for teacherId: " + teacherId + " and studentClass: " + studentClass);
        }
        return notesList.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<NotesDTO> getNotesByStudentClassAndSub(String studentClass, String sub) {
        List<Notes> notesList = notesRepository.findByStudentClassAndSub(studentClass, sub);
        if (notesList.isEmpty()) {
            throw new ResourceNotFoundException("No notes found for studentClass: " + studentClass + " and sub: " + sub);
        }
        return notesList.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }


}
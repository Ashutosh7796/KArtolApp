package com.spring.jwt.Notes;

import com.spring.jwt.exception.ResourceNotFoundException;
import com.spring.jwt.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/////////////////////////////////////////////////////////////////////////////////////
//
//      File Name    : NotesController
//      Description  : To perform CRUD operations for notes
//      Author       : Tushar Kapadnis
//      Date         : 26/06/2025
//
////////////////////////////////////////////////////////////////////////////////////
@RestController
@RequestMapping("/api/v1/notes")
@Tag(name = "Notes Management", description = "APIs for managing notes")
@Validated
@RequiredArgsConstructor
public class NotesController {

    private final NotesService notesService;

    @Operation(summary = "Create a new note", description = "Creates and saves a new note")
    @PostMapping
    public ResponseEntity<ApiResponse<NotesDTO>> createNote(
            @Parameter(description = "Note details", required = true)
            @Valid @RequestBody NotesDTO noteDto
    ) {
        try {
            NotesDTO result = notesService.createNote(noteDto);
            return ResponseEntity.ok(ApiResponse.success("Note created successfully", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error(HttpStatus.BAD_REQUEST, "Failed to create note", e.getMessage()));
        }
    }

    @Operation(summary = "Get a note by ID", description = "Retrieves a note by its ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<NotesDTO>> getNoteById(
            @Parameter(description = "Note ID", required = true, example = "1")
            @PathVariable @Min(1) Long id
    ) {
        try {
            NotesDTO result = notesService.getNoteById(id);
            return ResponseEntity.ok(ApiResponse.success("Note retrieved successfully", result));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponse.error(HttpStatus.NOT_FOUND, "Note not found", e.getMessage()));
        }
    }

    @Operation(summary = "Get all notes", description = "Retrieves a list of all notes")
    @GetMapping
    public ResponseEntity<ApiResponse<List<NotesDTO>>> getAllNotes() {
        try {
            List<NotesDTO> result = notesService.getAllNotes();
            return ResponseEntity.ok(ApiResponse.success("All notes fetched successfully", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error(HttpStatus.BAD_REQUEST, "Failed to fetch notes", e.getMessage()));
        }
    }

    @Operation(
            summary = "Partially update a note",
            description = "Updates one or more fields of an existing note by ID"
    )
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<NotesDTO>> patchNote(
            @Parameter(description = "Note ID", required = true, example = "1")
            @PathVariable @Min(1) Long id,

            @Parameter(description = "Fields to update in the note", required = true)
            @RequestBody NotesDTO noteDto
    ) {
        try {
            NotesDTO updatedNote = notesService.patchNote(id, noteDto);
            return ResponseEntity.ok(ApiResponse.success("Note updated successfully", updatedNote));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND, "Note not found", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST, "Failed to update note", e.getMessage()));
        }
    }


    @Operation(summary = "Delete a note", description = "Deletes a note by its ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteNote(
            @Parameter(description = "Note ID", required = true, example = "1")
            @PathVariable @Min(1) Long id
    ) {
        try {
            notesService.deleteNote(id);
            return ResponseEntity.ok(ApiResponse.success("Note deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponse.error(HttpStatus.NOT_FOUND, "Failed to delete note", e.getMessage()));
        }
    }

    @Operation(summary = "Get notes by teacherId and studentClass", description = "Fetch notes for a specific teacher and class")
    @GetMapping("/filter-by-teacher-class")
    public ResponseEntity<ApiResponse<List<NotesDTO>>> getNotesByTeacherIdAndClass(
            @RequestParam Integer teacherId,
            @RequestParam String studentClass
    ) {
        try {
            List<NotesDTO> result = notesService.getNotesByTeacherIdAndClass(teacherId, studentClass);
            return ResponseEntity.ok(ApiResponse.success("Notes fetched by teacherId and studentClass", result));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND, "No notes found", e.getMessage()));
        }
    }

    @Operation(summary = "Get notes by class and subject", description = "Fetch notes for a specific class and subject")
    @GetMapping("/filter-by-class-subject")
    public ResponseEntity<ApiResponse<List<NotesDTO>>> getNotesByStudentClassAndSub(
            @RequestParam String studentClass,
            @RequestParam String sub
    ) {
        try {
            List<NotesDTO> result = notesService.getNotesByStudentClassAndSub(studentClass, sub);
            return ResponseEntity.ok(ApiResponse.success("Notes fetched by studentClass and subject", result));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND, "No notes found", e.getMessage()));
        }
    }
}

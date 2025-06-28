package com.spring.jwt.Notes;


import com.spring.jwt.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notes")
@Tag(name = "Notes", description = "APIs for managing Notes")
public class NotesController {

    @Autowired
    private NotesService notesService;


    @Operation(summary = "Creating and saving new notes")
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<NotesDto>> createNotes(@Parameter(description = "Notes details required", required = true)
                                                             @Valid @RequestBody NotesDto notesDto) {
        try {
            NotesDto notes = notesService.createNotes(notesDto);
            return ResponseEntity.ok(ApiResponse.success("Notes created successfully", notes));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(HttpStatus.BAD_REQUEST, "Failed to create notes", e.getMessage()));
        }

    }
}

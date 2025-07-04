package com.spring.jwt.PaperPattern;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/paper-patterns")
@Tag(name = "Paper Pattern Controller", description = "APIs for managing paper patterns")
public class PaperPatternController {

    @Autowired
    private PaperPatternService paperPatternService;

    @Operation(summary = "Create a new paper pattern")
    @PostMapping("/add")
    public ResponseEntity<?> createPaperPattern(
            @Parameter(description = "PaperPattern details", required = true)
            @RequestBody PaperPatternDto paperPatternDto) {
        try {
            PaperPatternDto created = paperPatternService.createPaperPattern(paperPatternDto);
            return ResponseEntity.ok(created);
        } catch (Exception ex) {
            return buildErrorResponse("Failed to create paper pattern", ex);
        }
    }

    @Operation(summary = "Get a paper pattern by ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> getPaperPatternById(
            @Parameter(description = "ID of the paper pattern to retrieve", example = "1")
            @PathVariable Integer id) {
        try {
            PaperPatternDto dto = paperPatternService.getPaperPatternById(id);
            return ResponseEntity.ok(dto);
        } catch (Exception ex) {
            return buildErrorResponse("Failed to get paper pattern", ex);
        }
    }

    @Operation(summary = "Get all paper patterns")
    @GetMapping
    public ResponseEntity<?> getAllPaperPatterns() {
        try {
            List<PaperPatternDto> patterns = paperPatternService.getAllPaperPatterns();
            return ResponseEntity.ok(patterns);
        } catch (Exception ex) {
            return buildErrorResponse("Failed to fetch paper patterns", ex);
        }
    }

    @Operation(summary = "Update a paper pattern")
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePaperPattern(
            @Parameter(description = "ID of the paper pattern to update", example = "1")
            @PathVariable Integer id,
            @Parameter(description = "Updated paper pattern details")
            @RequestBody PaperPatternDto paperPatternDto) {
        try {
            PaperPatternDto updated = paperPatternService.updatePaperPattern(id, paperPatternDto);
            return ResponseEntity.ok(updated);
        } catch (Exception ex) {
            return buildErrorResponse("Failed to update paper pattern", ex);
        }
    }

    @Operation(summary = "Delete a paper pattern by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePaperPattern(
            @Parameter(description = "ID of the paper pattern to delete", example = "1")
            @PathVariable Integer id) {
        try {
            paperPatternService.deletePaperPattern(id);
            return ResponseEntity.noContent().build();
        } catch (Exception ex) {
            return buildErrorResponse("Failed to delete paper pattern", ex);
        }
    }

    // Utility method for error response
    private ResponseEntity<Map<String, Object>> buildErrorResponse(String message, Exception ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", new Date());
        body.put("status", 400);
        body.put("error", message);
        body.put("message", ex.getMessage());
        return ResponseEntity.badRequest().body(body);
    }
}
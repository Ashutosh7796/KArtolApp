package com.spring.jwt.PaperPattern;

import com.spring.jwt.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/PaperPattern")
@Tag(name = "PaperPattern", description = "APi's for managing paper pattern")
public class PaperPatternController {

    @Autowired
    private PaperPatternService paperPatternService;

    @Operation(summary = "Creates a new Pattern")
    @PostMapping("/savePattern")
    public ResponseEntity<ApiResponse<PaperPatternDto>> savePaperPattern(
            @Parameter(description = "Paper pattern details required", required = true)
            @Valid @RequestBody PaperPatternDto paperPatternDto) {

        try {
            PaperPatternDto paperPattern = paperPatternService.createPaperPattern(paperPatternDto);
            return ResponseEntity.ok(ApiResponse.success("Paper pattern saved successfully", paperPattern));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(HttpStatus.BAD_REQUEST, "Failed to create Paper pattern", e.getMessage()));
        }

    }

    @Operation(summary = "Get paper pattern by ID", description = "Retrieves an paper pattern by its unique identifier")
    @GetMapping("/byId/{id}")
    public ResponseEntity<ApiResponse<PaperPatternDto>> getPatternById(
            @Parameter(description = "PaperPattern id", required = true, example = "1")
            @PathVariable @Min(1) Integer id) {
        try {
            PaperPatternDto paperPatternById = paperPatternService.getPaperPatternById(id);
            return ResponseEntity.ok(ApiResponse.success("pattern fetched successfully", paperPatternById));
        } catch (PaperPatternNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND, "Pattern not found", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST, "Failed to fetch pattern", e.getMessage()));
        }
    }

    @Operation(summary = "Update a pattern", description = "Updates an existing pattern by ID")
    @PatchMapping("/update/{id}")
    public ResponseEntity<ApiResponse<PaperPatternDto>> updatePattern(
            @Parameter(description = "Pattern ID", required = true, example = "1")
            @PathVariable @Min(1) Integer id,
            @Parameter(description = "Pattern details to update", required = true)
            @Valid @RequestBody PaperPatternDto paperPatternDto
    ) {
        try {
            PaperPatternDto updatedPattern = paperPatternService.updatePaperPattern(id, paperPatternDto);
            return ResponseEntity.ok(ApiResponse.success("Pattern updated successfully", updatedPattern));
        } catch (PaperPatternNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND, "Pattern not found", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST, "Failed to update Pattern", e.getMessage()));
        }
    }

    @Operation(summary = "Delete a Pattern", description = "Deletes a pattern by ID")
    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<Void>> deletePattern(
            @Parameter(description = "Pattern Id", required = true, example = "1")
            @RequestParam @Min(1) Integer id) {
        try {
            paperPatternService.deletePaperPattern(id);
            return ResponseEntity.ok(ApiResponse.success("Pattern Deleted Successfully"));
        } catch (PaperPatternNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND, "Pattern not found", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST, "Failed to delete Pattern", e.getMessage()));
        }
    }
}

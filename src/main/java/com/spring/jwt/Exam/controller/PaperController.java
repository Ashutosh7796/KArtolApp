package com.spring.jwt.Exam.controller;

import com.spring.jwt.Exam.Dto.PaperDTO;
import com.spring.jwt.Exam.Dto.PaperDTO1;
import com.spring.jwt.Exam.Dto.PaperWithQuestionsDTO;
import com.spring.jwt.Exam.service.PaperService;
import com.spring.jwt.dto.PageResponseDto;
import com.spring.jwt.dto.ResponseDto;
import com.spring.jwt.exception.ResourceNotFoundException;
import com.spring.jwt.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.spring.jwt.Exam.Dto.PaperWithQuestionsAndAnswersDTO;

import java.util.List;

@Tag(name = "Paper Management", description = "APIs for managing exam papers")
@RestController
@RequestMapping("/api/v1/papers")
public class PaperController {

    @Autowired
    private PaperService paperService;



    @Operation(
            summary = "Create a new paper",
            description = "Creates a new paper with the given details"
    )
    @PostMapping("/add")
    public ResponseEntity<ResponseDto<PaperDTO>> createPaper(@RequestBody PaperDTO paperDTO) {
        try {
            PaperDTO paper = paperService.createPaper(paperDTO);
            return ResponseEntity.ok(
                    ResponseDto.success("Paper created successfully", paper)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ResponseDto.error("Failed to create paper", e.getMessage())
            );
        }
    }


    @Operation(
            summary = "Get Paper by ID",
            description = "Retrieves a paper by its unique identifier"
    )
    @GetMapping("/{id}")
    public ResponseEntity<?> getPaper(
            @Parameter(description = "Paper ID", required = true, example = "1")
            @PathVariable Integer id
    ) {
        try {
            PaperDTO1 paper = paperService.getPaper(id);
            return ResponseEntity.ok(paper);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Paper not found with id: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to get paper. " + e.getMessage());
        }
    }

    @Operation(
            summary = "Update Paper",
            description = "Updates an existing paper by its unique identifier"
    )
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePaper(
            @Parameter(description = "Paper ID", required = true, example = "1")
            @PathVariable Integer id,
            @RequestBody PaperDTO paperDTO
    ) {
        try {
            PaperDTO updated = paperService.updatePaper(id, paperDTO);
            return ResponseEntity.ok(updated);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Paper not found with id: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update paper. " + e.getMessage());
        }
    }

    @Operation(
            summary = "Delete Paper",
            description = "Deletes a paper by its unique identifier"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePaper(
            @Parameter(description = "Paper ID", required = true, example = "1")
            @PathVariable Integer id
    ) {
        try {
            paperService.deletePaper(id);
            return ResponseEntity.ok("Paper deleted successfully.");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Paper not found with id: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete paper. " + e.getMessage());
        }
    }

    @Operation(
            summary = "Get Paper with Questions (No Answers)",
            description = "Retrieves a paper and its questions by paper ID, excluding answers"
    )
    @GetMapping("/noanswer/{id}")
    public ResponseEntity<?> getPaperWithQuestions(
            @Parameter(description = "Paper ID", required = true, example = "1")
            @PathVariable Integer id
    ) {
        try {
            PaperWithQuestionsDTO dto = paperService.getPaperWithQuestions(id);
            return ResponseEntity.ok(dto);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Paper not found with id: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to get paper with questions. " + e.getMessage());
        }
    }

    @Operation(
            summary = "Get All Papers (Paged)",
            description = "Returns a paginated list of all papers"
    )
    @GetMapping("/papers")
    public ResponseEntity<?> getAllPapers(
            @Parameter(description = "Page number", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            PageResponseDto<PaperDTO> result = paperService.getAllPapers(page, size);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to fetch all papers. " + e.getMessage());
        }
    }

    @Operation(
            summary = "Get Live Papers for a Student Class",
            description = "Returns live papers for the specified student class"
    )
    @GetMapping("/papers/live")
    public ResponseEntity<?> getLivePapers(
            @Parameter(description = "Student Class", required = true, example = "10th")
            @RequestParam String studentClass
    ) {
        try {
            List<PaperDTO> papers = paperService.getLivePapers(studentClass);
            return ResponseEntity.ok(papers);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No live papers found for studentClass: " + studentClass);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to fetch live papers. " + e.getMessage());
        }
    }

    @GetMapping("/solutions")
    @Operation(
            summary = "Get paper with all questions, answers, and hints",
            description = "Retrieves a paper by ID including all its questions along with their correct answers and hints/solutions."
    )
    public ResponseEntity<ApiResponse<PaperWithQuestionsAndAnswersDTO>> getPaperWithSolutions(
            @Parameter(description = "ID of the paper to retrieve", required = true)
            @RequestParam Integer paperId
    ) {
        try {
            PaperWithQuestionsAndAnswersDTO response = paperService.getPaperWithSolutions(paperId);
            return ResponseEntity.ok(ApiResponse.success("Paper fetched successfully", response));
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST, "Failed to fetch paper", e.getMessage()));
        }
    }

}
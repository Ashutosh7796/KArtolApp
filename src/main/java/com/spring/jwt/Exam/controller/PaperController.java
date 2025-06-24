package com.spring.jwt.Exam.controller;

import com.spring.jwt.Exam.Dto.PaperDTO;
import com.spring.jwt.Exam.Dto.PaperWithQuestionsDTO;
import com.spring.jwt.Exam.service.PaperService;
import com.spring.jwt.dto.PageResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/papers")
public class PaperController {

    @Autowired
    private PaperService paperService;

    // Create Paper
    @PostMapping("/add")
    public PaperDTO createPaper(@RequestBody PaperDTO paperDTO) {
        return paperService.createPaper(paperDTO);
    }

    // Get Paper by id
    @GetMapping("/{id}")
    public PaperDTO getPaper(@PathVariable Integer id) {
        return paperService.getPaper(id);
    }

    // Update Paper
    @PutMapping("/{id}")
    public PaperDTO updatePaper(@PathVariable Integer id, @RequestBody PaperDTO paperDTO) {
        return paperService.updatePaper(id, paperDTO);
    }

    // Delete Paper
    @DeleteMapping("/{id}")
    public void deletePaper(@PathVariable Integer id) {
        paperService.deletePaper(id);
    }

    @GetMapping("/noanswer/{id}")
    public PaperWithQuestionsDTO getPaperWithQuestions(@PathVariable Integer id) {
        return paperService.getPaperWithQuestions(id);
    }

    @GetMapping("/papers")
    public ResponseEntity<PageResponseDto<PaperDTO>> getAllPapers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(paperService.getAllPapers(page, size));
    }

    @GetMapping("/papers/live")
    public ResponseEntity<List<PaperDTO>> getLivePapers() {
        List<PaperDTO> livePapers = paperService.getLivePapers();
        return ResponseEntity.ok(livePapers);
    }

}

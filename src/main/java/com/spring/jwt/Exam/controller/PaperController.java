package com.spring.jwt.Exam.controller;

import com.spring.jwt.Exam.Dto.PaperDTO;
import com.spring.jwt.Exam.Dto.PaperWithQuestionsDTO;
import com.spring.jwt.Exam.service.PaperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/papers")
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

    // Get all Papers
    @GetMapping
    public List<PaperDTO> getAllPapers() {
        return paperService.getAllPapers();
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
}
package com.spring.jwt.Question;

import com.spring.jwt.entity.Question;
import com.spring.jwt.utils.GenericResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/questions")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @PostMapping("/add")
    public ResponseEntity<GenericResponseDto<Question>> createQuestion(@RequestBody Question question) {
        try {
            Question created = questionService.createQuestion(question);
            return ResponseEntity.ok(GenericResponseDto.success("Question created successfully", created));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(GenericResponseDto.error("Failed to create question", e.getMessage()));
        }
    }

    @GetMapping("/getById")
    public ResponseEntity<GenericResponseDto<Question>> getQuestionById(@RequestParam Integer id) {
        try {
            Question question = questionService.getQuestionById(id);
            return ResponseEntity.ok(GenericResponseDto.success("Question fetched successfully", question));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(GenericResponseDto.error("Failed to fetch question", e.getMessage()));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<GenericResponseDto<List<Question>>> getAllQuestions() {
        try {
            List<Question> questions = questionService.getAllQuestions();
            return ResponseEntity.ok(GenericResponseDto.success("All questions fetched successfully", questions));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(GenericResponseDto.error("Failed to fetch questions", e.getMessage()));
        }
    }

    @PatchMapping("/update")
    public ResponseEntity<GenericResponseDto<Question>> updateQuestion(
            @RequestParam Integer id,
            @RequestBody Question updatedQuestion) {
        try {
            Question updated = questionService.updateQuestion(id, updatedQuestion);
            return ResponseEntity.ok(GenericResponseDto.success("Question updated successfully", updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(GenericResponseDto.error("Failed to update question", e.getMessage()));
        }
    }

    @DeleteMapping("delete")
    public ResponseEntity<GenericResponseDto<Void>> deleteQuestion(@RequestParam Integer id) {
        try {
            questionService.deleteQuestion(id);
            return ResponseEntity.ok(GenericResponseDto.success("Question deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(GenericResponseDto.error("Failed to delete question", e.getMessage()));
        }
    }

    @GetMapping("/user")
    public ResponseEntity<GenericResponseDto<List<Question>>> getQuestionsByUserId(@RequestParam Integer userId) {
        try {
            List<Question> questions = questionService.getQuestionsByUserId(userId);
            return ResponseEntity.ok(GenericResponseDto.success("Questions fetched for userId " + userId, questions));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(GenericResponseDto.error("Failed to fetch questions by userId", e.getMessage()));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<GenericResponseDto<List<Question>>> getQuestionsBySubTypeLevelMarks(
            @RequestParam(required = false) String sub,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String marks
    ) {
        try {
            List<Question> questions = questionService.getQuestionsBySubTypeLevelMarks(sub, type, level, marks);
            return ResponseEntity.ok(GenericResponseDto.success("Questions fetched by criteria", questions));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(GenericResponseDto.error("Failed to fetch questions by criteria", e.getMessage()));
        }
    }
}
package com.spring.jwt.Assessment;

import com.spring.jwt.dto.ResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/assessments")
public class AssessmentController {

    @Autowired
    private AssessmentService assessmentService;

    @PostMapping("/create")
    public ResponseEntity<ResponseDto<List<AssessmentDTO>>> createAssessment(
            @RequestBody AssessmentDTO dto
    ) {
        try {
            List<AssessmentDTO> result = assessmentService.createAssessmentsBulk(dto);
            return ResponseEntity.ok(ResponseDto.success("Assessment created successfully", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseDto.error("Failed to create assessment", e.getMessage()));
        }
    }

    @GetMapping("/getByIdWithAns")
    public ResponseEntity<ResponseDto<AssessmentDTO>> getAssessmentById(@RequestParam Integer id) {
        try {
            AssessmentDTO dto = assessmentService.getAssessmentById(id);
            return ResponseEntity.ok(ResponseDto.success("Assessment fetched successfully", dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseDto.error("Failed to fetch assessment", e.getMessage()));
        }
    }
    @GetMapping("/getById")
    public ResponseEntity<ResponseDto<AssessmentDtoWithoutAns>> getAssessmentByIdWithoutAns(@RequestParam Integer id) {
        try {
            AssessmentDtoWithoutAns dto = assessmentService.getAssessmentByIdWithoutAns(id);
            return ResponseEntity.ok(ResponseDto.success("Assessment fetched successfully", dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseDto.error("Failed to fetch assessment", e.getMessage()));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<ResponseDto<List<AssessmentDtoWithoutAns>>> getAllAssessments() {
        try {
            List<AssessmentDtoWithoutAns> result = assessmentService.getAllAssessments();
            return ResponseEntity.ok(ResponseDto.success("All assessments fetched successfully", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseDto.error("Failed to fetch assessments", e.getMessage()));
        }
    }

    @PatchMapping("/update")
    public ResponseEntity<ResponseDto<AssessmentDTO>> updateAssessment(
            @RequestParam Integer id,
            @RequestBody AssessmentDTO dto
    ) {
        try {
            AssessmentDTO updated = assessmentService.updateAssessment(id, dto);
            return ResponseEntity.ok(ResponseDto.success("Assessment updated successfully", updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseDto.error("Failed to update assessment", e.getMessage()));
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ResponseDto<Void>> deleteAssessment(@RequestParam Integer id) {
        try {
            assessmentService.deleteAssessment(id);
            return ResponseEntity.ok(ResponseDto.success("Assessment deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseDto.error("Failed to delete assessment", e.getMessage()));
        }
    }
}
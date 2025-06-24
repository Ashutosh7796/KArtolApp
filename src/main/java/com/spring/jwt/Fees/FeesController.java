package com.spring.jwt.Fees;
import com.spring.jwt.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/fees")
@RequiredArgsConstructor
public class FeesController {

    private final FeesService feesService;

    @PostMapping("/add")
    public ResponseEntity<ResponseDto<FeesDto>> createFees(@RequestBody FeesDto feesDto) {
        try {
            FeesDto created = feesService.createFees(feesDto);
            return ResponseEntity.ok(ResponseDto.success("Fees created successfully", created));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseDto.error("Failed to create fees", e.getMessage()));
        }
    }

    @GetMapping("/getById")
    public ResponseEntity<ResponseDto<FeesDto>> getFeesById(@RequestParam Integer feesId) {
        try {
            FeesDto fees = feesService.getFeesById(feesId);
            return ResponseEntity.ok(ResponseDto.success("Fees fetched successfully", fees));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseDto.error("Failed to fetch fees", e.getMessage()));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<ResponseDto<List<FeesDto>>> getAllFees() {
        try {
            List<FeesDto> feesList = feesService.getAllFees();
            return ResponseEntity.ok(ResponseDto.success("All fees fetched successfully", feesList));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseDto.error("Failed to fetch all fees", e.getMessage()));
        }
    }

    @PatchMapping("/update")
    public ResponseEntity<ResponseDto<FeesDto>> updateFees(
            @RequestParam Integer feesId,
            @RequestBody FeesDto updatedFeesDto) {
        try {
            FeesDto updated = feesService.updateFees(feesId, updatedFeesDto);
            return ResponseEntity.ok(ResponseDto.success("Fees updated successfully", updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseDto.error("Failed to update fees", e.getMessage()));
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ResponseDto<Void>> deleteFees(@RequestParam Integer feesId) {
        try {
            feesService.deleteFees(feesId);
            return ResponseEntity.ok(ResponseDto.success("Fees deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseDto.error("Failed to delete fees", e.getMessage()));
        }
    }
    @GetMapping("/status")
    public ResponseEntity<ResponseDto<List<FeesDto>>> getFeesByStatus(@RequestParam String status) {
        try {
            List<FeesDto> feesList = feesService.getByStatus(status);
            return ResponseEntity.ok(ResponseDto.success("Fees fetched by status successfully", feesList));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseDto.error("Failed to fetch fees by status", e.getMessage()));
        }
    }
}
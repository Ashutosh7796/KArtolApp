package com.spring.jwt.UserFee;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/userFee")
public class UserFeeController {

    private final UserFeeService userFeeService;

    public UserFeeController(UserFeeService userFeeService) {
        this.userFeeService = userFeeService;
    }
    @PostMapping("/pay")
    public ResponseEntity<UserFeeResponseDTO> saveUserFee(@RequestBody UserFeeCreateDTO userFeeDTO) {
        try {
            // Pass the instance, not the class name
            UserFeeDTO savedFee = userFeeService.saveUserFee(userFeeDTO);
            return ResponseEntity.ok(new UserFeeResponseDTO(true, "Fee payment saved successfully", savedFee));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new UserFeeResponseDTO(false, e.getMessage(), null));
        }
    }

    @PostMapping("/upgrade-class")
    public ResponseEntity<UserFeeResponseDTO> upgradeStudentClass(@RequestParam Integer userId, @RequestParam Integer newFeesId) {
        try {
            userFeeService.upgradeStudentClass(userId, newFeesId);
            return ResponseEntity.ok(new UserFeeResponseDTO(true, "Student class upgraded successfully", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new UserFeeResponseDTO(false, e.getMessage(), null));
        }
    }

    @GetMapping("/by-user/{userId}")
    public ResponseEntity<UserFeeResponseDTO> getByUserId(@PathVariable Integer userId) {
        try {
            var data = userFeeService.getUserFeesByUserId(userId);
            return ResponseEntity.ok(new UserFeeResponseDTO(true, "Fetched records", data));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(new UserFeeResponseDTO(false, ex.getMessage(), null));
        }
    }

    @GetMapping("/by-class/{studentClass}")
    public ResponseEntity<UserFeeResponseDTO> getByStudentClass(@PathVariable String studentClass) {
        try {
            var data = userFeeService.getByStudentClass(studentClass);
            return ResponseEntity.ok(new UserFeeResponseDTO(true, "Fetched records", data));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(new UserFeeResponseDTO(false, ex.getMessage(), null));
        }
    }

    @GetMapping("/by-class-batch")
    public ResponseEntity<UserFeeResponseDTO> getByClassAndBatch(
            @RequestParam String studentClass,
            @RequestParam String batch) {
        try {
            var data = userFeeService.getByStudentClassAndBatch(studentClass, batch);
            return ResponseEntity.ok(new UserFeeResponseDTO(true, "Fetched records", data));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(new UserFeeResponseDTO(false, ex.getMessage(), null));
        }
    }

    @GetMapping("/by-status/{status}")
    public ResponseEntity<UserFeeResponseDTO> getByStatus(@PathVariable String status) {
        try {
            var data = userFeeService.getByStatus(status);
            return ResponseEntity.ok(new UserFeeResponseDTO(true, "Fetched records", data));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(new UserFeeResponseDTO(false, ex.getMessage(), null));
        }
    }

    @GetMapping("/by-duration")
    public ResponseEntity<UserFeeResponseDTO> getByDuration(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            var data = userFeeService.getByDuration(startDate, endDate);
            return ResponseEntity.ok(new UserFeeResponseDTO(true, "Fetched records", data));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(new UserFeeResponseDTO(false, ex.getMessage(), null));
        }
    }

    @GetMapping("/by-class-status")
    public ResponseEntity<UserFeeResponseDTO> getByClassAndStatus(
            @RequestParam String studentClass,
            @RequestParam String status) {
        try {
            var data = userFeeService.getByStudentClassAndStatus(studentClass, status);
            return ResponseEntity.ok(new UserFeeResponseDTO(true, "Fetched records", data));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(new UserFeeResponseDTO(false, ex.getMessage(), null));
        }
    }
}

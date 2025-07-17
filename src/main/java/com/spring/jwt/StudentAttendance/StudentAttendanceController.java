package com.spring.jwt.StudentAttendance;

import com.spring.jwt.exception.ResourceNotFoundException;
import com.spring.jwt.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/attendance")
@Tag(name = "Student Attendance", description = "APIs for managing student attendance")
@Validated
@RequiredArgsConstructor
public class StudentAttendanceController {

    private static final Logger log = LoggerFactory.getLogger(StudentAttendanceController.class);

    private final StudentAttendanceService studentAttendanceService;

    @PostMapping("/add")
    @PermitAll
    @Operation(summary = "Create student attendance", description = "Creates a single student attendance record")
    public ResponseEntity<ApiResponse<StudentAttendanceDTO>> create(
            @Valid @RequestBody StudentAttendanceDTO dto) {
        try {
            StudentAttendanceDTO created = studentAttendanceService.create(dto);
            return ResponseEntity.ok(ApiResponse.success("Attendance created successfully", created));
        } catch (Exception e) {
            log.error("Failed to create attendance: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.error(HttpStatus.BAD_REQUEST, "Failed to create attendance", e.getMessage()));
        }
    }

    @PostMapping("/add/batch")
    @PermitAll
    @Operation(summary = "Create batch attendance", description = "Creates multiple student attendance records")
    public ResponseEntity<ApiResponse<Void>> createBatch(@Valid @RequestBody CreateStudentAttendanceDTO batchDto) {
        try {
            studentAttendanceService.createBatchAttendance(batchDto);
            return ResponseEntity.ok(ApiResponse.success("Batch attendance created successfully"));
        } catch (Exception e) {
            log.error("Failed to create batch attendance: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.error(HttpStatus.BAD_REQUEST, "Failed to create batch attendance", e.getMessage()));
        }
    }

    @GetMapping("/getById")
    @PermitAll
    @Operation(summary = "Get attendance by ID", description = "Retrieves a specific student attendance record by ID")
    public ResponseEntity<ApiResponse<StudentAttendanceDTO>> getById(
            @RequestParam @Min(1) Integer id) {
        try {
            StudentAttendanceDTO dto = studentAttendanceService.getById(id);
            return ResponseEntity.ok(ApiResponse.success("Attendance fetched successfully", dto));
        } catch (Exception e) {
            log.error("Failed to fetch attendance by ID: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND, "Attendance not found", e.getMessage()));
        }
    }

    @GetMapping("/all")
    @PermitAll
    @Operation(summary = "Get all attendance", description = "Fetches all student attendance records")
    public ResponseEntity<ApiResponse<List<StudentAttendanceDTO>>> getAll() {
        try {
            List<StudentAttendanceDTO> list = studentAttendanceService.getAll();
            return ResponseEntity.ok(ApiResponse.success("All attendance records fetched", list));
        } catch (Exception e) {
            log.error("Failed to fetch all attendance: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.error(HttpStatus.BAD_REQUEST, "Failed to fetch attendance records", e.getMessage()));
        }
    }

    @PatchMapping("/update")
    @PermitAll
    @Operation(summary = "Update attendance", description = "Updates an existing student attendance record")
    public ResponseEntity<ApiResponse<StudentAttendanceDTO>> update(
            @RequestParam @Min(1) Integer id,
            @Valid @RequestBody StudentAttendanceDTO dto) {
        try {
            StudentAttendanceDTO updated = studentAttendanceService.update(id, dto);
            return ResponseEntity.ok(ApiResponse.success("Attendance updated successfully", updated));
        } catch (Exception e) {
            log.error("Failed to update attendance: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.error(HttpStatus.BAD_REQUEST, "Failed to update attendance", e.getMessage()));
        }
    }

    @DeleteMapping("/delete")
    @PermitAll
    @Operation(summary = "Delete attendance", description = "Deletes a student attendance record by ID")
    public ResponseEntity<ApiResponse<Void>> delete(@RequestParam @Min(1) Integer id) {
        try {
            studentAttendanceService.delete(id);
            return ResponseEntity.ok(ApiResponse.success("Attendance deleted successfully"));
        } catch (Exception e) {
            log.error("Failed to delete attendance: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND, "Failed to delete attendance", e.getMessage()));
        }
    }

    @GetMapping("/user")
    @PermitAll
    @Operation(summary = "Get attendance by userId", description = "Fetches all attendance for a specific user")
    public ResponseEntity<ApiResponse<List<StudentAttendanceDTO>>> getByUserId(@RequestParam Integer userId) {
        try {
            List<StudentAttendanceDTO> list = studentAttendanceService.getByUserId(userId);
            return ResponseEntity.ok(ApiResponse.success("Attendance fetched for user", list));
        } catch (Exception e) {
            log.error("Failed to fetch attendance by userId: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.error(HttpStatus.BAD_REQUEST, "Failed to fetch attendance by userId", e.getMessage()));
        }
    }

    @GetMapping("/date")
    @PermitAll
    @Operation(summary = "Get attendance by date", description = "Fetches attendance records for a given date")
    public ResponseEntity<ApiResponse<List<StudentAttendanceDTO>>> getByDate(@RequestParam LocalDate date) {
        try {
            List<StudentAttendanceDTO> list = studentAttendanceService.getByDate(date);
            return ResponseEntity.ok(ApiResponse.success("Attendance fetched for date", list));
        } catch (Exception e) {
            log.error("Failed to fetch attendance by date: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.error(HttpStatus.BAD_REQUEST, "Failed to fetch attendance by date", e.getMessage()));
        }
    }

    @GetMapping("/sub")
    @PermitAll
    @Operation(summary = "Get attendance by subject", description = "Fetches attendance records for a specific subject")
    public ResponseEntity<ApiResponse<List<StudentAttendanceDTO>>> getBySub(@RequestParam String sub) {
        try {
            List<StudentAttendanceDTO> list = studentAttendanceService.getBySub(sub);
            return ResponseEntity.ok(ApiResponse.success("Attendance fetched for subject", list));
        } catch (Exception e) {
            log.error("Failed to fetch attendance by subject: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.error(HttpStatus.BAD_REQUEST, "Failed to fetch attendance by subject", e.getMessage()));
        }
    }

    @GetMapping("/teacher")
    @PermitAll
    @Operation(summary = "Get attendance by teacher", description = "Fetches attendance records for a teacher")
    public ResponseEntity<ApiResponse<List<StudentAttendanceDTO>>> getByTeacherId(@RequestParam Integer teacherId) {
        try {
            List<StudentAttendanceDTO> list = studentAttendanceService.getByTeacherId(teacherId);
            return ResponseEntity.ok(ApiResponse.success("Attendance fetched for teacher", list));
        } catch (Exception e) {
            log.error("Failed to fetch attendance by teacherId: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.error(HttpStatus.BAD_REQUEST, "Failed to fetch attendance by teacherId", e.getMessage()));
        }
    }

    @GetMapping("/class")
    @PermitAll
    @Operation(summary = "Get attendance by class", description = "Fetches attendance records for a class")
    public ResponseEntity<ApiResponse<List<StudentAttendanceDTO>>> getByStudentClass(@RequestParam String studentClass) {
        try {
            List<StudentAttendanceDTO> list = studentAttendanceService.getByStudentClass(studentClass);
            return ResponseEntity.ok(ApiResponse.success("Attendance fetched for class", list));
        } catch (Exception e) {
            log.error("Failed to fetch attendance by class: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.error(HttpStatus.BAD_REQUEST, "Failed to fetch attendance by class", e.getMessage()));
        }
    }

    @GetMapping("/search")
    @PermitAll
    @Operation(summary = "Search attendance", description = "Search attendance records by date, class, and teacher")
    public ResponseEntity<ApiResponse<List<StudentAttendanceDTO>>> getByDateAndStudentClassAndTeacherId(
            @RequestParam LocalDate date,
            @RequestParam String studentClass,
            @RequestParam Integer teacherId) {
        try {
            List<StudentAttendanceDTO> list = studentAttendanceService.getByDateAndStudentClassAndTeacherId(date, studentClass, teacherId);
            return ResponseEntity.ok(ApiResponse.success("Filtered attendance fetched", list));
        } catch (Exception e) {
            log.error("Failed to search attendance: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.error(HttpStatus.BAD_REQUEST, "Failed to search attendance", e.getMessage()));
        }
    }

    @GetMapping("/score")
    @PermitAll
    @Operation(summary = "Get Attendance Scores", description = "Returns total, weekly, monthly, and yearly attendance scores")
    public ResponseEntity<ApiResponse<AttendanceScoreDto>> getAttendanceScores(@RequestParam Integer userId) {
        try {
            AttendanceScoreDto scores = studentAttendanceService.getAttendanceScores(userId);
            return ResponseEntity.ok(ApiResponse.success("Attendance scores fetched successfully", scores));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    ApiResponse.error(HttpStatus.BAD_REQUEST, "Failed to fetch scores", e.getMessage())
            );
        }
    }



    @GetMapping("/attendance")
    @PermitAll
    @Operation(
            summary = "Get Attendance Records",
            description = "Returns all attendance records matching the given date, subject, teacher, and student class"
    )
    public ResponseEntity<ApiResponse<List<StudentAttendanceDTO>>> getAttendance(
            @RequestParam LocalDate date,
            @RequestParam String sub,
            @RequestParam Integer teacherId,
            @RequestParam String studentClass
    ) {
        try {
            List<StudentAttendanceDTO> results = studentAttendanceService.getByDateSubTeacherIdStudentClass(
                    date, sub, teacherId, studentClass
            );
            return ResponseEntity.ok(ApiResponse.success("Attendance records fetched successfully", results));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    ApiResponse.error(HttpStatus.BAD_REQUEST, "Failed to fetch attendance", e.getMessage())
            );
        }
    }

    @GetMapping("/attendance/summary/user/{userId}")
    public ResponseEntity<?> getAttendanceSummary(@PathVariable Integer userId) {
        try {
            StudentAttendanceSummaryResponseDto summary = studentAttendanceService.getSubjectWiseSummaryByUserId(userId);
            return ResponseEntity.ok(summary);
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error: " + ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to fetch attendance summary. Reason: " + ex.getMessage());
        }
    }

}
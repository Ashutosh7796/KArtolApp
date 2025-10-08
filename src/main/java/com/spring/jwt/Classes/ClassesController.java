package com.spring.jwt.Classes;
import com.spring.jwt.exception.ResourceNotFoundException;
import com.spring.jwt.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
@RestController
@RequestMapping("/api/v1/Classes")
@Tag(name = "Classes Management", description = "APIs for managing classes")
@Validated
@RequiredArgsConstructor
public class ClassesController {
    @Autowired
    private ClassesService classesService;

    @Operation(summary = "creates a new class")
    @PostMapping("/add")
    public ResponseEntity<ApiResponse<ClassesDto>> createClass(
            @Parameter(description = "Classes details", required = true)
            @Valid @RequestBody ClassesDto classesDto) {
        try {
            ClassesDto aClass = classesService.createClass(classesDto);
            return ResponseEntity.ok(ApiResponse.success("Class Created Successfully", aClass));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(HttpStatus.BAD_REQUEST, "Failed to create class", e.getMessage()));
        }
    }

    @Operation(summary = "Fetches Class", description = "Gets classes based on subject")
    @GetMapping("/getClass")
    public ResponseEntity<ApiResponse<List<ClassesDto>>> getClassBySubject(
            @Parameter(description = "name of subject", required = true)
            @RequestParam String sub,
            @Parameter(description = "class of student", required = true)
            @RequestParam String studentClass) {
        try {
            List<ClassesDto> classBySubject = classesService.getClassBySubject(sub, studentClass);
            return ResponseEntity.ok(ApiResponse.success("Class fetched successfully", classBySubject));
        } catch (ClassesNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(HttpStatus.NOT_FOUND, "Class not found", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(HttpStatus.BAD_REQUEST, "Failed fetching Classes", e.getMessage()));
        }
    }

    @Operation(summary = "updates existing class", description = "updates class details using id")
    @PatchMapping("/update/{id}")
    public ResponseEntity<ApiResponse<ClassesDto>> updateClasses(
            @Parameter(description = "unique id of classes", required = true)
            @PathVariable @Min(1) Long id,
            @Parameter(description = "Classes details", required = true)
            @RequestBody ClassesDto classesDto) {
        try {
            ClassesDto classesDto1 = classesService.updateClass(id, classesDto);
            return ResponseEntity.ok(ApiResponse.success("Classes updated successfully", classesDto1));
        } catch (ClassesNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(HttpStatus.NOT_FOUND, "Class not found", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(HttpStatus.BAD_REQUEST, "Failed to update class", e.getMessage()));
        }
    }

    @Operation(summary = "Deletes a class using id")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteClass(
            @Parameter(description = "Unique id of class", required = true, example = "1")
            @PathVariable @Min(1) Long id) {
        try {
            classesService.deleteClass(id);
            return ResponseEntity.ok(ApiResponse.success("Class deleted successfully"));
        } catch (ClassesNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(HttpStatus.NOT_FOUND, "Class not found", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(HttpStatus.BAD_REQUEST, "Failed to delete a class", e.getMessage()));
        }
    }

    @GetMapping("/today")
    public ResponseEntity<ApiResponse<List<ClassesDetailsDto>>> getClassesBySubjectToday(
            @RequestParam String sub,
            @RequestParam String studentClass,
            @RequestParam String date) {

        try {
            LocalDate parsedDate = LocalDate.parse(date);
            List<ClassesDetailsDto> result = classesService.getClassBySubjectToday(sub, studentClass, parsedDate);

            return ResponseEntity.ok(ApiResponse.success("Classes fetched successfully", result));

        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND, "No classes found", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Error fetching classes", e.getMessage()));
        }
    }

    @GetMapping("/upcoming")
    public ResponseEntity<ApiResponse<List<ClassesDto>>> getTodayClasses(@RequestParam String studentClass) {
        try {
            if (studentClass == null || studentClass.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error(HttpStatus.BAD_REQUEST, "studentClass parameter is required and cannot be empty", "Invalid input"));
            }

            List<ClassesDto> result = classesService.getTodayClassesByStudentClass(studentClass);
            return ResponseEntity.ok(ApiResponse.success("Upcoming classes fetched successfully", result));

        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND, "No upcoming classes found", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Error fetching upcoming classes", e.getMessage()));
        }
    }

    @Operation(summary = "Get all unique subject names", description = "Fetches distinct subjects from the Classes table")
    @GetMapping("/subjects")
    public ResponseEntity<ApiResponse<List<String>>> getUniqueSubjects() {
        try {
            List<String> subjects = classesService.getUniqueSubjects();
            return ResponseEntity.ok(ApiResponse.success("Unique subjects fetched successfully", subjects));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND, "No subjects found", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to fetch unique subjects", e.getMessage()));
        }
    }


}
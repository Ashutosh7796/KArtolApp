package com.spring.jwt.Student;

import com.spring.jwt.dto.ResponseDto;
import com.spring.jwt.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @GetMapping("/getById/{id}")
    public ResponseEntity<ResponseDto<StudentDto>> getStudentById(@PathVariable Integer id) {
        StudentDto student = studentService.getStudentById(id);
        return ResponseEntity.ok(ResponseDto.success("Student fetched successfully", student));
    }

    @GetMapping("getAll")
    public ResponseEntity<ResponseDto<List<StudentDto>>> getAllStudents() {
        List<StudentDto> students = studentService.getAllStudents();
        return ResponseEntity.ok(ResponseDto.success("All students fetched successfully", students));
    }

//    @PutMapping("/{id}")
//    public ResponseEntity<ResponseDto<StudentDto>> updateStudent(@PathVariable Integer id,
//                                                                 @RequestBody StudentDto studentDto) {
//        StudentDto updated = studentService.updateStudent(id, studentDto);
//        return ResponseEntity.ok(ResponseDto.success("Student updated successfully", updated));
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<ResponseDto<Void>> deleteStudent(@PathVariable Integer id) {
//        studentService.deleteStudent(id);
//        return ResponseEntity.ok(ResponseDto.success("Student deleted successfully with id: " + id, null));
//    }

    @GetMapping("/filter")
    public ResponseEntity<ResponseDto<List<StudentDto>>> getStudentsByClassAndBatch(
            @RequestParam(required = false) String studentClass,
            @RequestParam(required = false) String batch) {
        List<StudentDto> students = studentService.getStudentsByClassAndBatch(studentClass, batch);
        return ResponseEntity.ok(ResponseDto.success("Students fetched successfully", students));
    }

    @GetMapping("/parent")
    public ResponseEntity<ResponseDto<List<StudentInfo>>> getStudentsByParent(@RequestParam Integer parentId) {
        try {
            List<StudentInfo> studentInfo = studentService.getStudentId(parentId);
            return ResponseEntity.ok(
                    ResponseDto.success("Students fetched successfully", studentInfo)
            );
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ResponseDto.error("Failed to fetch students", ex.getMessage())
            );
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ResponseDto.error("Something went wrong", ex.getMessage())
            );
        }
    }

}

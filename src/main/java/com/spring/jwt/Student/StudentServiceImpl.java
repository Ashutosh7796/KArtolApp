package com.spring.jwt.Student;

import com.spring.jwt.entity.Student;
import com.spring.jwt.exception.ResourceNotFoundException;
import com.spring.jwt.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;

    private StudentDto mapToDto(Student student) {
        return new StudentDto(
                student.getStudentId(),
                student.getName(),
                student.getLastName(),
                student.getDateOfBirth(),
                student.getAddress(),
                student.getBatch(),
                student.getStudentcol1(),
                student.getStudentClass(),
                student.getUserId(),
                student.getParentsId()
        );
    }

    private Student mapToEntity(StudentDto dto) {
        return new Student(
                dto.getStudentId(),
                dto.getName(),
                dto.getLastName(),
                dto.getDateOfBirth(),
                dto.getAddress(),
                dto.getBatch(),
                dto.getStudentcol1(),
                dto.getStudentClass(),
                dto.getUserId(),
                dto.getParentsId()
        );
    }


    @Override
    public StudentDto getStudentById(Integer id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException("Student not found with id: " + id));
        return mapToDto(student);
    }

    @Override
    public List<StudentDto> getAllStudents() {
        return studentRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public StudentDto updateStudent(Integer id, StudentDto studentDto) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException("Student not found with id: " + id));

        student.setName(studentDto.getName());
        student.setLastName(studentDto.getLastName());
        student.setDateOfBirth(studentDto.getDateOfBirth());
        student.setAddress(studentDto.getAddress());
        student.setBatch(studentDto.getBatch());
        student.setStudentcol1(studentDto.getStudentcol1());
        student.setStudentClass(studentDto.getStudentClass());
        student.setUserId(studentDto.getUserId());

        return mapToDto(studentRepository.save(student));
    }

    @Override
    public void deleteStudent(Integer id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException("Student not found with id: " + id));
        studentRepository.delete(student);
    }

    @Override
    public List<StudentDto> getStudentsByClassAndBatch(String studentClass, String batch) {
        if (studentClass == null || batch == null) {
            throw new ResourceNotFoundException("Both studentClass and batch must be provided");
        }

        List<Student> students = studentRepository.findByStudentClassAndBatch(studentClass, batch);

        if (students.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No students found for class: " + studentClass + " and batch: " + batch
            );
        }

        return students.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<StudentInfo> getStudentId(Integer parentsId) {
        List<Student> students = studentRepository.findByParentsId(parentsId);

        if (students.isEmpty()) {
            throw new ResourceNotFoundException("No students found for parentId: " + parentsId);
        }

        return students.stream()
                .map(s -> new StudentInfo(
                        s.getUserId(),
                        s.getName() + " " + s.getLastName(),
                        s.getStudentClass(),
                        s.getBatch()
                ))
                .collect(Collectors.toList());
    }

}

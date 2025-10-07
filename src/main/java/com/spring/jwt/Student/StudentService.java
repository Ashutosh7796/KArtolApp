package com.spring.jwt.Student;

import java.util.List;

public interface StudentService {

    StudentDto getStudentById(Integer id);

    List<StudentDto> getAllStudents();

    StudentDto updateStudent(Integer id, StudentDto studentDto);

    void deleteStudent(Integer id);

    List<StudentDto> getStudentsByClassAndBatch(String studentClass, String batch);

    List<StudentInfo>getStudentId(Integer parentsId);

}

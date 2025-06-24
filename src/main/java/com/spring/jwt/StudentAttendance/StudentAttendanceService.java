package com.spring.jwt.StudentAttendance;

import java.util.Date;
import java.util.List;

public interface StudentAttendanceService {
    StudentAttendanceDTO create(StudentAttendanceDTO dto);
    StudentAttendanceDTO getById(Integer id);
    List<StudentAttendanceDTO> getAll();
    StudentAttendanceDTO update(Integer id, StudentAttendanceDTO dto);
    void delete(Integer id);
     void createBatchAttendance(CreateStudentAttendanceDTO batchDto);

    List<StudentAttendanceDTO> getByUserId(Integer userId);
    List<StudentAttendanceDTO> getByDate(Date date);
    List<StudentAttendanceDTO> getBySub(String sub);
    List<StudentAttendanceDTO> getByTeacherId(Integer teacherId);
    List<StudentAttendanceDTO> getByStudentClass(String studentClass);
    List<StudentAttendanceDTO> getByDateAndStudentClassAndTeacherId(Date date, String studentClass, Integer teacherId);

}
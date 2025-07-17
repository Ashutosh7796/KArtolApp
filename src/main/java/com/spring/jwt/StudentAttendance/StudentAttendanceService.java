package com.spring.jwt.StudentAttendance;

import com.spring.jwt.entity.StudentAttendance;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface StudentAttendanceService {
    StudentAttendanceDTO create(StudentAttendanceDTO dto);
    StudentAttendanceDTO getById(Integer id);
    List<StudentAttendanceDTO> getAll();
    StudentAttendanceDTO update(Integer id, StudentAttendanceDTO dto);
    void delete(Integer id);
//     void createBatchAttendance(CreateStudentAttendanceDTO batchDto);
 List<StudentAttendance> createBatchAttendance(CreateStudentAttendanceDTO batchDto);
    List<StudentAttendanceDTO> getByUserId(Integer userId);
    List<StudentAttendanceDTO> getByDate(LocalDate date);
    List<StudentAttendanceDTO> getBySub(String sub);
    List<StudentAttendanceDTO> getByTeacherId(Integer teacherId);
    List<StudentAttendanceDTO> getByStudentClass(String studentClass);
    List<StudentAttendanceDTO> getByDateAndStudentClassAndTeacherId(LocalDate date, String studentClass, Integer teacherId);

    AttendanceScoreDto getAttendanceScores(Integer userId);



    List<StudentAttendanceDTO> getByDateSubTeacherIdStudentClass(LocalDate date, String sub, Integer teacherId, String studentClass);

    StudentAttendanceSummaryResponseDto getSubjectWiseSummaryByUserId(Integer userId);
}
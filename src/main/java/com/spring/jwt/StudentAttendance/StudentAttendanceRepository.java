package com.spring.jwt.StudentAttendance;


import com.spring.jwt.entity.StudentAttendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface StudentAttendanceRepository extends JpaRepository<StudentAttendance, Integer> {

    List<StudentAttendance> findByDateAndStudentClassAndTeacherId(LocalDate date, String studentClass, Integer teacherId);

    List<StudentAttendance> findByStudentClass(String studentClass);

    List<StudentAttendance> findByTeacherId(Integer teacherId);

    List<StudentAttendance> findBySub(String sub);

    List<StudentAttendance> findByDate(LocalDate date);

    List<StudentAttendance> findByUserId(Integer userId);

    List<StudentAttendance> findByUserIdAndDateBetween(Integer userId, LocalDate startDate, LocalDate endDate);


}
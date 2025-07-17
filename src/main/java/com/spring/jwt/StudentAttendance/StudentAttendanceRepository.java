package com.spring.jwt.StudentAttendance;


import com.spring.jwt.entity.StudentAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface StudentAttendanceRepository extends JpaRepository<StudentAttendance, Integer> {

    List<StudentAttendance> findByDateAndStudentClassAndTeacherId(LocalDate date, String studentClass, Integer teacherId);

    List<StudentAttendance> findByStudentClass(String studentClass);

    List<StudentAttendance> findByTeacherId(Integer teacherId);

    List<StudentAttendance> findBySub(String sub);

    List<StudentAttendance> findByDate(LocalDate date);

    List<StudentAttendance> findByUserId(Integer userId);

    List<StudentAttendance> findByUserIdAndDateBetween(Integer userId, LocalDate startDate, LocalDate endDate);

    // In StudentAttendanceRepository
    Optional<StudentAttendance> findByStudentClassAndTeacherIdAndSubAndDate(
            String studentClass, Integer teacherId, String sub, LocalDate date);

    Optional<StudentAttendance> findByDateAndSubAndUserIdAndTeacherId(
            LocalDate date, String sub, Long userId, Integer teacherId
    );
    List<StudentAttendance> findByDateAndSubAndTeacherIdAndStudentClass(
            LocalDate date, String sub, Integer teacherId, String studentClass
    );

    @Query("SELECT s.sub AS subject, " +
            "SUM(CASE WHEN s.attendance = true THEN 1 ELSE 0 END) AS presentCount, " +
            "SUM(CASE WHEN s.attendance = false THEN 1 ELSE 0 END) AS absentCount, " +
            "COUNT(s) AS totalCount " +
            "FROM StudentAttendance s " +
            "WHERE s.userId = :userId " +
            "GROUP BY s.sub")
    List<Object[]> getAttendanceSummaryByUserId(@Param("userId") Integer userId);

    StudentAttendance findFirstByUserId(Integer userId);


}
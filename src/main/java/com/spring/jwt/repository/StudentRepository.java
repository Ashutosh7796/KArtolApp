package com.spring.jwt.repository;

import com.spring.jwt.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {
    Student findByUserId(Integer userId);
    List<Student> findByStudentClass(String studentClass);
}
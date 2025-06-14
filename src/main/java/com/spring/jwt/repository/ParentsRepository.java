package com.spring.jwt.repository;

import com.spring.jwt.entity.Parents;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParentsRepository extends JpaRepository<Parents, Integer> {
    Parents findByStudentId(Integer studentId);
} 
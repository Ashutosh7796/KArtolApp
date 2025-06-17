package com.spring.jwt.Exam.repository;

import com.spring.jwt.Exam.entity.Paper;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaperRepository extends JpaRepository<Paper, Integer> {
}
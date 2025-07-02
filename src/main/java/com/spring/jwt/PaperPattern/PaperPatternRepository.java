package com.spring.jwt.PaperPattern;


import com.spring.jwt.entity.PaperPattern;
import org.springframework.data.jpa.repository.JpaRepository;
public interface PaperPatternRepository extends JpaRepository<PaperPattern, Integer> {}
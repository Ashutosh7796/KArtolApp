package com.spring.jwt.Fees;

import com.spring.jwt.entity.Fees;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeesRepository extends JpaRepository<Fees, Integer> {
    List<Fees> findByStatus(String status);

    List<Fees> findByStudentClass(String studentClass);

    Fees findByStudentClassAndBatch(String studentClass, String batch);
}
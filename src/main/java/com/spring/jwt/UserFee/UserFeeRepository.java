package com.spring.jwt.UserFee;

import com.spring.jwt.entity.UserFee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserFeeRepository extends JpaRepository<UserFee, Integer> {
    List<UserFee> findByUserId(Integer userId); // Get all payments for a student
    List<UserFee> findByFeesId(Integer feesId); // Get all payments for a specific fee structure
    List<UserFee> findByUserIdOrderByDateDesc(Integer userId); // Payments history, newest first

    List<UserFee> findByStudentClass(String studentClass);

    List<UserFee> findByStudentClassAndBatch(String studentClass, String batch);

    List<UserFee> findByStatus(String status);

    List<UserFee> findByDateBetween(String startDate, String endDate);

    List<UserFee> findByStudentClassAndStatus(String studentClass, String status);
}

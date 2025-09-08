package com.spring.jwt.UserFee;

import java.util.List;

public interface UserFeeService {
    UserFeeDTO saveUserFee(UserFeeCreateDTO dto);
    List<UserFeeDTO> getUserFeesByUserId(Integer userId);
    void upgradeStudentClass(Integer userId, Integer newFeesId);

    List<UserFeeDTO> getByStudentClass(String studentClass);
    List<UserFeeDTO> getByStudentClassAndBatch(String studentClass, String batch);
    List<UserFeeDTO> getByStatus(String status);
    List<UserFeeDTO> getByDuration(String startDate, String endDate);
    List<UserFeeDTO> getByStudentClassAndStatus(String studentClass, String status);
}


package com.spring.jwt.Fees;

import java.util.List;

public interface FeesService {
    FeesDto createFees(FeesDto feesDto);
    FeesDto getFeesById(Integer feesId);
    List<FeesDto> getAllFees();
    FeesDto updateFees(Integer feesId, FeesDto feesDto);
    void deleteFees(Integer feesId);
    List<FeesDto> getByStatus(String status);
}
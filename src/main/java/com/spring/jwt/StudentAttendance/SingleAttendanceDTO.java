package com.spring.jwt.StudentAttendance;

import lombok.Data;

@Data
public class SingleAttendanceDTO {
    private Integer userId;
    private Boolean attendance;
}

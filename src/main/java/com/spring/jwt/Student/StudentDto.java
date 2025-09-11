package com.spring.jwt.Student;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentDto {

    private Integer studentId;
    private String name;
    private String lastName;
    private String dateOfBirth;
    private String address;
    private String batch;
    private String studentcol1;
    private String studentClass;
    private Integer userId;

}

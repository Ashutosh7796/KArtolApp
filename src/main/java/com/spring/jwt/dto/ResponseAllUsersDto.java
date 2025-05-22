package com.spring.jwt.dto;

import lombok.Data;

import java.util.List;

@Data
public class ResponseAllUsersDto {


    private String message;
    private List<UserDTO> list;
    private String exception;
    private int PageSize;
    private int TotalPages;

    public ResponseAllUsersDto(String message){
        this.message=message;
    }
    public ResponseAllUsersDto(String message, List<UserDTO> list) {
        this.message = message;
        this.list = list;
    }

}
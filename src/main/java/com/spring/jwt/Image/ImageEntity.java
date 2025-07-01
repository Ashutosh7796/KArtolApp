package com.spring.jwt.Image;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageEntity {


        @Id
        @GeneratedValue
        private Long id;
        @Column(name = "name")
        private String name;
        @Column(name = "email")
        private String email;
        @Column(name = "mobile")
        private String mobile;
        @Column(name = "address")
        private String address;
        @Column(name = "dateOfBirth")
        private LocalDate dateOfBirth;
        @Lob
        @Column(columnDefinition = "LONGBLOB")
        private byte[] image;

    }



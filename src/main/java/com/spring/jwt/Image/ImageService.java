package com.spring.jwt.Image;


import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;

public interface ImageService {
    String updateImage(Long id, MultipartFile file) throws IOException;
    String saveImage(MultipartFile file, String name, String email, String mobile, String address, LocalDate dateOfBirth);

    byte[] getImage(Long id);

    String deleteImage(Long id);

}
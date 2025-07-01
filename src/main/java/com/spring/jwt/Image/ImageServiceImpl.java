package com.spring.jwt.Image;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;

    @Override
    public String saveImage(MultipartFile file, String name, String email, String mobile, String address, LocalDate dateOfBirth) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Thumbnails.of(file.getInputStream())
                    .size(500, 500)
                    .outputFormat("jpg")
                    .outputQuality(0.7)
                    .toOutputStream(outputStream);

            byte[] compressedImage = outputStream.toByteArray();

            ImageEntity imageEntity = new ImageEntity();
            imageEntity.setName(name);
            imageEntity.setEmail(email);
            imageEntity.setMobile(mobile);
            imageEntity.setAddress(address);
            imageEntity.setDateOfBirth(dateOfBirth);
            imageEntity.setImage(compressedImage);

            imageRepository.save(imageEntity);

            return "Image saved successfully with ID: " + imageEntity.getId();
        } catch (IOException e) {
            throw new RuntimeException("Failed to save image", e);
        }
    }

    @Override
    public byte[] getImage(Long id) {
        ImageEntity imageEntity = imageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Image not found"));
        return imageEntity.getImage();
    }

    @Override
    public String deleteImage(Long id) {
        if (!imageRepository.existsById(id)) {
            throw new ImageNotFoundException("Image not found");
        }
        imageRepository.deleteById(id);
        return "Image deleted successfully with ID: " + id;
    }


    @Override
    public String updateImage(Long id, MultipartFile file) throws IOException {
        try {
            ImageEntity imageEntity = imageRepository.findById(id)
                    .orElseThrow(() -> new ImageNotFoundException("Image not found"));

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            Thumbnails.of(file.getInputStream())
                    .size(500, 500)
                    .outputFormat("jpg")
                    .outputQuality(0.7)
                    .toOutputStream(outputStream);

            byte[] compressedImage = outputStream.toByteArray();

            imageEntity.setImage(compressedImage);

            imageRepository.save(imageEntity);

            return "Image updated successfully";
        } catch (IOException e) {
            throw new RuntimeException("Failed to process image", e);
        }
    }


    }

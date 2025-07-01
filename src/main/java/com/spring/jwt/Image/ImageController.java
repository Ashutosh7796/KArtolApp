package com.spring.jwt.Image;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDate;

@RestController
@RequestMapping("/Image")
@RequiredArgsConstructor
@Tag(name = "Image Management", description = "APIs for managing imageData")
public class ImageController {

    @Autowired
    private ImageService imageService;

    @Operation(summary = "saving image with other details",description = "uploads a image with name,email,mobile,address and DOB")
    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file, @RequestParam("name") String name,
                                         @RequestParam("email")String email, @RequestParam("mobile") String mobile,
                                              @RequestParam("address") String address,
                                         @RequestParam ("dateOfBirth")@DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate dateOfBirth) {


        imageService.saveImage(file,name,email,mobile,address,dateOfBirth);

        return ResponseEntity.ok("Image Compressed and Saved Successfully");

    }

    @Operation(summary = "getting image with id",description = "retrieving image with its unique identifier")
    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getImage(@PathVariable Long id) {
        byte[] image = imageService.getImage(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG_VALUE)
                .body(image);
    }

    @Operation(summary = "Updating image using id", description = "updates image using its unique identifier")
    @PatchMapping("/update/{id}")
    public ResponseEntity<String> updateImage(@PathVariable Long id,
                                              @RequestParam("file") MultipartFile file) throws IOException {
        String message = imageService.updateImage(id, file);

        return ResponseEntity.ok("Image updated successfully");
    }

    @Operation(summary = "Deleting image",description = "deletes image using its id")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteImage(@PathVariable Long id) {
        imageService.deleteImage(id);
        return ResponseEntity.ok("image deleted successfully");

      }

    }

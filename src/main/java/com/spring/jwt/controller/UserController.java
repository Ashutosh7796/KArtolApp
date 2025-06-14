package com.spring.jwt.controller;


import com.spring.jwt.dto.ResetPassword;
import com.spring.jwt.dto.ResponseAllUsersDto;
import com.spring.jwt.dto.UserDTO;
import com.spring.jwt.dto.UserUpdateRequest;
import com.spring.jwt.dto.UserProfileDTO;
import com.spring.jwt.entity.User;
import com.spring.jwt.jwt.JwtService;
import com.spring.jwt.repository.UserRepository;
import com.spring.jwt.service.UserService;
import com.spring.jwt.utils.BaseResponseDTO;
import com.spring.jwt.utils.EncryptionUtil;
import com.spring.jwt.utils.ErrorResponseDto;
import com.spring.jwt.utils.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Tag(
        name = "CRUD REST APIs for User in AutoCarCare",
        description = "CRUD REST APIs in AutoCarCare to CREATE, UPDATE, FETCH AND DELETE USER Details"
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@Slf4j
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final EncryptionUtil encryptionUtil;
    private final JwtService jwtService;
    
    @Value("${app.url.password-reset}")
    private String passwordResetUrl;

    @Operation(
            summary = "Create User Account REST API",
            description = "REST API to create new user account in AutoCarCare"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "HTTP Status CREATED"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Mobile Number is already registered Or Email Number is already registered",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Email Id Not Verified",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    }
    )

    @PostMapping("/registerUser")
    public ResponseEntity<BaseResponseDTO> register(@Valid @RequestBody UserDTO userDTO){
        return ResponseEntity.ok(userService.registerAccount(userDTO));
    }

    @Operation(
            summary = "Forgot Password REST API",
            description = "REST API to Reset Password inside AutoCarCare"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "OTP sent successfully."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Email field is empty",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    }
    )

    @PostMapping("/forgot-password")
    public ResponseEntity<ResponseDto> forgotPassword(HttpServletRequest request) {
        String email = request.getParameter("email");
        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ResponseDto("Unsuccessful", "Email is required"));
        }

        ResponseDto response = userService.handleForgotPassword(email, request.getServerName());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/reset-password")
    public ResponseEntity<String> getResetPasswordPage(@RequestParam String token) {
        try {
            if (!userService.validateResetToken(token)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired token");
            }
            
            ClassPathResource resource = new ClassPathResource("templates/reset-password.html");
            String htmlContent = Files.readString(Paths.get(resource.getURI()), StandardCharsets.UTF_8);
            return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(htmlContent);
        } catch (IOException e) {
            log.error("Error loading reset password template", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error loading HTML file");
        }
    }

    @PostMapping("/update-password")
    public ResponseEntity<ResponseDto> updatePassword(@Valid @RequestBody ResetPassword request) {

        ResponseDto response = userService.processPasswordUpdate(request);
        return ResponseEntity.ok(response);
    }


    @Operation(
            summary = "GetAllUsers REST API",
            description = "REST API to Fetch All Users List inside AutoCarCare"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "OK"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not Found",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    }
    )

    @GetMapping("/getAllUsers")
    public ResponseEntity<ResponseAllUsersDto> getAllUsers(
            @RequestParam(defaultValue= "0") int pageNo, 
            @RequestParam(defaultValue= "10") int pageSize) {
        
        Page<UserDTO> userPage = userService.getAllUsers(pageNo, pageSize);

        List<UserDTO> decryptedUsers = userPage.getContent().stream()
            .map(user -> {
                try {
                    if (user.getFirstName() != null) {
                        user.setFirstName(encryptionUtil.decrypt(user.getFirstName()));
                        log.debug("Decrypted firstName: {}", user.getFirstName());
                    }
                    if (user.getLastName() != null) {
                        user.setLastName(encryptionUtil.decrypt(user.getLastName()));
                        log.debug("Decrypted lastName: {}", user.getLastName());
                    }
                    if (user.getAddress() != null) {
                        user.setAddress(encryptionUtil.decrypt(user.getAddress()));
                        log.debug("Decrypted address: {}", user.getAddress());
                    }
                } catch (Exception e) {
                    log.error("Error decrypting user data: {}", e.getMessage());
                }
                return user;
            })
            .collect(java.util.stream.Collectors.toList());
        
        ResponseAllUsersDto response = new ResponseAllUsersDto("success", decryptedUsers);
        response.setTotalPages(userPage.getTotalPages());
        response.setPageSize(userPage.getSize());
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get User Details REST API",
            description = "REST API to Fetch Single User Details inside AutoCarCare"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "OK"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User Not Found",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    }
    )

    @GetMapping("getUser/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @Operation(
            summary = "Get User Profile REST API",
            description = "REST API to Fetch User Profile with role-specific data"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "OK"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User Not Found",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    }
    )
    @GetMapping("/profile/{id}")
    public ResponseEntity<UserProfileDTO> getUserProfile(@PathVariable Long id) {
        UserProfileDTO profile = userService.getUserProfileById(id);

        try {
            UserDTO user = profile.getUser();
            if (user != null) {
                if (user.getFirstName() != null) {
                    user.setFirstName(encryptionUtil.decrypt(user.getFirstName()));
                }
                if (user.getLastName() != null) {
                    user.setLastName(encryptionUtil.decrypt(user.getLastName()));
                }
                if (user.getAddress() != null) {
                    user.setAddress(encryptionUtil.decrypt(user.getAddress()));
                }
            }
        } catch (Exception e) {
            log.error("Error decrypting user data: {}", e.getMessage());
        }
        
        return ResponseEntity.ok(profile);
    }
    
    @Operation(
            summary = "Get Current User Profile REST API",
            description = "REST API to Fetch Current User Profile with role-specific data"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "OK"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    }
    )
    @GetMapping("/profile")
    public ResponseEntity<UserProfileDTO> getCurrentUserProfile() {
        UserProfileDTO profile = userService.getCurrentUserProfile();
        
        try {
            UserDTO user = profile.getUser();
            if (user != null) {
                if (user.getFirstName() != null) {
                    user.setFirstName(encryptionUtil.decrypt(user.getFirstName()));
                }
                if (user.getLastName() != null) {
                    user.setLastName(encryptionUtil.decrypt(user.getLastName()));
                }
                if (user.getAddress() != null) {
                    user.setAddress(encryptionUtil.decrypt(user.getAddress()));
                }
            }
        } catch (Exception e) {
            log.error("Error decrypting user data: {}", e.getMessage());
        }
        
        return ResponseEntity.ok(profile);
    }

    @Operation(
            summary = "Update User Details REST API",
            description = "REST API to Update User Details List inside AutoCarCare"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "OK"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User Not Found",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    }
    )

    @PatchMapping("updateDetails/{id}")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable Long id, @Valid @RequestBody UserUpdateRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @GetMapping("/check-device-fingerprint")
    public ResponseEntity<Map<String, Object>> checkDeviceFingerprint(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated() || 
                    authentication instanceof AnonymousAuthenticationToken) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "User not authenticated"));
            }
            
            String username = authentication.getName();
            User user = userRepository.findByEmail(username);
            
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "User not found"));
            }

            String storedFingerprint = user.getDeviceFingerprint();

            String currentFingerprint = jwtService.generateDeviceFingerprint(request);
            
            result.put("email", user.getEmail());
            result.put("lastLogin", user.getLastLogin());

            if (storedFingerprint != null) {
                result.put("storedFingerprint", storedFingerprint.substring(0, 8) + "...");
            } else {
                result.put("storedFingerprint", null);
            }
            
            if (currentFingerprint != null) {
                result.put("currentFingerprint", currentFingerprint.substring(0, 8) + "...");
            } else {
                result.put("currentFingerprint", null);
            }
            
            boolean fingerprintsMatch = storedFingerprint != null && 
                    currentFingerprint != null && 
                    storedFingerprint.equals(currentFingerprint);
            
            result.put("fingerprintsMatch", fingerprintsMatch);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error checking device fingerprint: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error checking device fingerprint: " + e.getMessage()));
        }
    }
}

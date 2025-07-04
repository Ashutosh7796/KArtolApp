package com.spring.jwt.dto;

import com.spring.jwt.entity.Role;
import com.spring.jwt.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    @Schema(
            description = "Email of User", example = "example@example.com"
    )
    private String email;

    @Schema(
            description = "userId of User", example = "10011"
    )
    private String userId;

    @Schema(
            description = "Password to create an account", example = "Pass@1234"
    )
    private String password;

    @Schema(
            description = "Address of the customer", example = "A/P Pune Main Street Block no 8"
    )
    private String address;

    @Schema(
            description = "First Name of the customer", example = "John"
    )
    private String firstName;

    @Schema(
            description = "Last Name of the customer", example = "Doe"
    )
    private String lastName;

    @Schema(
            description = "Mobile Number of the customer", example = "9822222212"
    )
    private Long mobileNumber;

    @Schema(
            description = "Roles of the User", example = "[\"USER\", \"ADMIN\"]"
    )
    private Set<String> roles;

    private String name;
    private String dateOfBirth;
    private String studentcol;
    private String studentcol1;
    private String studentClass;
    private String role; // Single role field for backward compatibility

    public UserDTO(User user) {
        this.email = user.getEmail();
        this.address = user.getAddress();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.mobileNumber = user.getMobileNumber();
        this.userId = user.getId().toString();
        
        if (user.getRoles() != null) {
            this.roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
        }
    }

    // Static method to create DTO from User entity
    public static UserDTO fromEntity(User user) {
        if (user == null) {
            return null;
        }

        UserDTO dto = new UserDTO();
        dto.setEmail(user.getEmail());
        dto.setMobileNumber(user.getMobileNumber());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setAddress(user.getAddress());
        dto.setUserId(user.getId().toString());
        
        if (user.getRoles() != null) {
            dto.setRoles(user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet()));
        }
        
        return dto;
    }
}

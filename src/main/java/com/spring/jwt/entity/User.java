package com.spring.jwt.entity;

import com.spring.jwt.utils.encrypt.AttributeEncryptor;
import com.spring.jwt.utils.encrypt.EncryptedField;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @SequenceGenerator(name = "user_seq", sequenceName = "user_sequence", allocationSize = 1, initialValue = 10000)
    @Column(name = "user_id")
    private Integer id;

    @NotBlank(message = "Email cannot be blank")
    @Email
    @Pattern(regexp = "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$", message = "Invalid email format")
    @Column(name = "email", nullable = false, length = 250, unique = true)
    private String email;

    @Column(name = "first_name", length = 512)
    @Convert(converter = AttributeEncryptor.class)
    @EncryptedField
    private String firstName;

    @Column(name = "last_name", length = 512)
    @Convert(converter = AttributeEncryptor.class)
    @EncryptedField
    private String lastName;

    @Column(name = "reset_password_token")
    private String resetPasswordToken;

    @Column(name = "address", length = 1024)
    @Convert(converter = AttributeEncryptor.class)
    @EncryptedField
    private String Address;

    @Column(name = "mobile_number")
    private Long mobileNumber;

    @Column(name = "password")
    private String password;

    @Column(name = "reset_password_token_expiry")
    private LocalDateTime resetPasswordTokenExpiry;
    
    @Column(name = "device_fingerprint", length = 1024)
    private String deviceFingerprint;
    
    @Column(name = "last_login")
    private LocalDateTime lastLogin;
    
    @Column(name = "login_attempts")
    private Integer loginAttempts = 0;
    
    @Column(name = "account_locked_until")
    private LocalDateTime accountLockedUntil;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "user_id"),
    inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "role_id"))
    private Set<Role> roles;


}

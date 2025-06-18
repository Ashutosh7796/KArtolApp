package com.spring.jwt.service.impl;

import com.spring.jwt.dto.UserDTO;
import com.spring.jwt.entity.Role;
import com.spring.jwt.entity.User;
import com.spring.jwt.exception.BaseException;
import com.spring.jwt.exception.UserNotFoundExceptions;
import com.spring.jwt.mapper.UserMapper;
import com.spring.jwt.repository.RoleRepository;
import com.spring.jwt.repository.UserRepository;
import com.spring.jwt.utils.BaseResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserDTO userDTO;
    private Role role;

    @BeforeEach
    void setUp() {
        // Setup mock data
        role = new Role();
        role.setId(1L);
        role.setName("USER");

        user = new User();
        user.setId(1);
        user.setEmail("test@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setPassword("encodedPassword");
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);
        user.setEmailVerified(true);

        userDTO = new UserDTO();
        userDTO.setEmail("test@example.com");
        userDTO.setFirstName("Test");
        userDTO.setLastName("User");
        userDTO.setPassword("password");
        userDTO.setRole("USER");
    }

    @Test
    @DisplayName("Should register a new user successfully")
    void shouldRegisterUser() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(null);
        when(roleRepository.findByName(anyString())).thenReturn(role);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        BaseResponseDTO result = userService.registerAccount(userDTO);

        // Assert
        assertNotNull(result);
        assertEquals("200", result.getCode());
        
        // Verify repository interactions
        verify(userRepository).findByEmail("test@example.com");
        verify(roleRepository).findByName("USER");
        verify(passwordEncoder).encode("password");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when user email already exists")
    void shouldThrowExceptionWhenEmailExists() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(user);

        // Act & Assert
        assertThrows(BaseException.class, () -> userService.registerAccount(userDTO));
        
        // Verify repository was checked but no save occurred
        verify(userRepository).findByEmail("test@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should find user by ID")
    void shouldFindUserById() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userMapper.toDTO(any(User.class))).thenReturn(userDTO);

        // Act
        UserDTO result = userService.getUserById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        
        // Verify repository was checked
        verify(userRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when user ID not found")
    void shouldThrowExceptionWhenUserIdNotFound() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundExceptions.class, () -> userService.getUserById(1L));
        
        // Verify repository was checked
        verify(userRepository).findById(1L);
    }
} 
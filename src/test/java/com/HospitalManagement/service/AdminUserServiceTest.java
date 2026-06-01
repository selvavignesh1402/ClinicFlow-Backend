package com.HospitalManagement.service;

import com.HospitalManagement.entity.User;
import com.HospitalManagement.enums.Roles;
import com.HospitalManagement.repository.UserRepository;
import com.HospitalManagement.requestdto.AdminCreateUserRequestDto;
import com.HospitalManagement.requestdto.AdminUpdateUserRequestDto;
import com.HospitalManagement.responsedto.UserResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Admin User Service Tests")
class AdminUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdminUserService adminUserService;

    private User staffUser;
    private AdminCreateUserRequestDto createRequestDto;
    private AdminUpdateUserRequestDto updateRequestDto;

    @BeforeEach
    void setUp() {
        staffUser = User.builder()
                .userId(10L)
                .name("Dr. Asha Mehta")
                .email("asha.mehta@clinicflow.com")
                .password("encodedPassword")
                .phone("9876500001")
                .role(Roles.CLINICIAN)
                .status("ACTIVE")
                .build();

        createRequestDto = new AdminCreateUserRequestDto(
                "Dr. Asha Mehta",
                "asha.mehta@clinicflow.com",
                "plainPassword",
                "9876500001",
                "CLINICIAN",
                "ACTIVE"
        );

        updateRequestDto = new AdminUpdateUserRequestDto(
                "Dr. Asha Mehta - Updated",
                "asha.mehta@clinicflow.com",
                "newPlainPassword",
                "9876500001",
                "CLINICIAN",
                "ACTIVE"
        );
    }

    @Test
    @DisplayName("Should retrieve all users successfully")
    void testGetAllUsers() {
        // Arrange
        when(userRepository.findAll()).thenReturn(Arrays.asList(staffUser));

        // Act
        List<UserResponseDto> result = adminUserService.getAllUsers();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Dr. Asha Mehta", result.get(0).name());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should retrieve user by ID successfully")
    void testGetUserById() {
        // Arrange
        when(userRepository.findById(10L)).thenReturn(Optional.of(staffUser));

        // Act
        UserResponseDto result = adminUserService.getUserById(10L);

        // Assert
        assertNotNull(result);
        assertEquals(10L, result.userId());
        assertEquals("Dr. Asha Mehta", result.name());
        verify(userRepository, times(1)).findById(10L);
    }

    @Test
    @DisplayName("Should throw exception when user not found by ID")
    void testGetUserByIdNotFound() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> adminUserService.getUserById(999L));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    @DisplayName("Should create staff user successfully")
    void testCreateUserSuccess() {
        // Arrange
        when(userRepository.existsByEmail("asha.mehta@clinicflow.com")).thenReturn(false);
        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(staffUser);

        // Act
        UserResponseDto result = adminUserService.createUser(createRequestDto);

        // Assert
        assertNotNull(result);
        assertEquals("asha.mehta@clinicflow.com", result.email());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when creating user with duplicate email")
    void testCreateUserDuplicateEmail() {
        // Arrange
        when(userRepository.existsByEmail("asha.mehta@clinicflow.com")).thenReturn(true);

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> adminUserService.createUser(createRequestDto));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when creating user with invalid role")
    void testCreateUserInvalidRole() {
        // Arrange
        AdminCreateUserRequestDto invalidRequest = new AdminCreateUserRequestDto(
                "Joe Staff", "joe@example.com", "pass", "123", "INVALID_ROLE", "ACTIVE"
        );

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> adminUserService.createUser(invalidRequest));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Invalid role"));
    }

    @Test
    @DisplayName("Should throw exception when creating PATIENT role through admin API")
    void testCreateUserPatientRoleForbidden() {
        // Arrange
        AdminCreateUserRequestDto patientRequest = new AdminCreateUserRequestDto(
                "Jane Patient", "jane@example.com", "pass", "123", "PATIENT", "ACTIVE"
        );

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> adminUserService.createUser(patientRequest));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Use /api/v1/auth/register for patient self-registration"));
    }

    @Test
    @DisplayName("Should update user successfully")
    void testUpdateUserSuccess() {
        // Arrange
        when(userRepository.findById(10L)).thenReturn(Optional.of(staffUser));
        when(userRepository.existsByEmailAndUserIdNot("asha.mehta@clinicflow.com", 10L)).thenReturn(false);
        when(passwordEncoder.encode("newPlainPassword")).thenReturn("encodedPasswordUpdated");
        when(userRepository.save(any(User.class))).thenReturn(staffUser);

        // Act
        UserResponseDto result = adminUserService.updateUser(10L, updateRequestDto);

        // Assert
        assertNotNull(result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should deactivate user successfully")
    void testDeactivateUser() {
        // Arrange
        when(userRepository.findById(10L)).thenReturn(Optional.of(staffUser));
        when(userRepository.save(any(User.class))).thenReturn(staffUser);

        // Act
        adminUserService.deactivateUser(10L);

        // Assert
        assertEquals("INACTIVE", staffUser.getStatus());
        verify(userRepository, times(1)).save(staffUser);
    }
}

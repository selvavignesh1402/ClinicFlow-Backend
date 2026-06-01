package com.HospitalManagement.service;

import com.HospitalManagement.entity.User;
import com.HospitalManagement.enums.Roles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JWT Service Tests")
class JwtServiceTest {

    private JwtService jwtService;
    private User user;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();

        // Inject configuration values using ReflectionTestUtils
        ReflectionTestUtils.setField(jwtService, "secretKey", "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970");
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 86400000L); // 24 hours

        user = User.builder()
                .userId(1L)
                .name("Jane Doe")
                .email("jane.doe@example.com")
                .role(Roles.PATIENT)
                .status("ACTIVE")
                .build();
    }

    @Test
    @DisplayName("Should generate a valid JWT token with custom claims")
    void testGenerateToken() {
        // Act
        String token = jwtService.generateToken(user);

        // Assert
        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    @DisplayName("Should extract username (email) from token successfully")
    void testExtractUsername() {
        // Arrange
        String token = jwtService.generateToken(user);

        // Act
        String extractedUser = jwtService.extractUsername(token);

        // Assert
        assertEquals("jane.doe@example.com", extractedUser);
    }

    @Test
    @DisplayName("Should validate correct token successfully")
    void testIsTokenValid() {
        // Arrange
        String token = jwtService.generateToken(user);

        // Act
        boolean isValid = jwtService.isTokenValid(token, user);

        // Assert
        assertTrue(isValid);
    }

    @Test
    @DisplayName("Should invalidate token when username does not match user details")
    void testIsTokenInvalidMismatchedUsername() {
        // Arrange
        String token = jwtService.generateToken(user);
        User mismatchedUser = User.builder()
                .userId(2L)
                .email("mismatch@example.com")
                .role(Roles.PATIENT)
                .build();

        // Act
        boolean isValid = jwtService.isTokenValid(token, mismatchedUser);

        // Assert
        assertFalse(isValid);
    }
}

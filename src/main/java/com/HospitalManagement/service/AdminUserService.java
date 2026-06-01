package com.HospitalManagement.service;

import com.HospitalManagement.entity.User;
import com.HospitalManagement.enums.Roles;
import com.HospitalManagement.repository.UserRepository;
import com.HospitalManagement.requestdto.AdminCreateUserRequestDto;
import com.HospitalManagement.requestdto.AdminUpdateUserRequestDto;
import com.HospitalManagement.responsedto.UserResponseDto;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class AdminUserService {

    private static final Logger logger = LoggerFactory.getLogger(AdminUserService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminUserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<UserResponseDto> getAllUsers() {
        logger.debug("Fetching all users");
        List<UserResponseDto> users = userRepository.findAll()
                .stream()
                .map(this::toResponseDto)
                .toList();
        logger.info("Retrieved {} users", users.size());
        return users;
    }

    @Transactional(readOnly = true)
    public UserResponseDto getUserById(Long userId) {
        logger.debug("Fetching user with ID: {}", userId);
        UserResponseDto user = toResponseDto(findUser(userId));
        logger.info("Retrieved user - ID: {}, Email: {}, Role: {}, Status: {}", userId, user.email(), user.role(), user.status());
        return user;
    }

    public UserResponseDto createUser(AdminCreateUserRequestDto requestDto) {
        logger.info("Creating new user - Name: {}, Email: {}, Role: {}", requestDto.name(), requestDto.email(), requestDto.role());
        if (userRepository.existsByEmail(requestDto.email())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "User already exists with email: " + requestDto.email());
        }

        Roles role = parseRole(requestDto.role());
        ensureStaffRole(role);

        User user = User.builder()
                .name(requestDto.name())
                .email(requestDto.email())
                .password(passwordEncoder.encode(requestDto.password()))
                .phone(requestDto.phone())
                .role(role)
                .status(normalizeStatus(requestDto.status()))
                .build();

        return toResponseDto(userRepository.save(user));
    }

    public UserResponseDto updateUser(Long userId, AdminUpdateUserRequestDto requestDto) {
        logger.info("Updating user - UserID: {}, NewEmail: {}, NewRole: {}", userId, requestDto.email(), requestDto.role());
        User user = findUser(userId);
        if (userRepository.existsByEmailAndUserIdNot(requestDto.email(), userId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "User already exists with email: " + requestDto.email());
        }

        Roles role = parseRole(requestDto.role());
        ensureStaffRole(role);
        user.setName(requestDto.name());
        user.setEmail(requestDto.email());
        user.setPhone(requestDto.phone());
        user.setRole(role);
        user.setStatus(normalizeStatus(requestDto.status()));
        if (requestDto.password() != null && !requestDto.password().isBlank()) {
            user.setPassword(passwordEncoder.encode(requestDto.password()));
        }

        return toResponseDto(userRepository.save(user));
    }

    public void deactivateUser(Long userId) {
        logger.info("Deactivating user - ID: {}", userId);
        User user = findUser(userId);
        user.setStatus("INACTIVE");
        userRepository.save(user);
        logger.info("User deactivated successfully - ID: {}, Email: {}", userId, user.getEmail());
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found with id: " + userId));
    }

    private Roles parseRole(String role) {
        try {
            return Roles.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid role: " + role);
        }
    }

    private String normalizeStatus(String status) {
        if (status == null || status.isBlank()) {
            return "ACTIVE";
        }
        return status.toUpperCase();
    }

    private void ensureStaffRole(Roles role) {
        if (role == Roles.PATIENT) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Use /api/v1/auth/register for patient self-registration");
        }
    }

    private UserResponseDto toResponseDto(User user) {
        return new UserResponseDto(
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole() != null ? user.getRole().name() : null,
                user.getStatus(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
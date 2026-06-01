package com.HospitalManagement.service;

import com.HospitalManagement.entity.Appointment;
import com.HospitalManagement.entity.Patient;
import com.HospitalManagement.entity.User;
import com.HospitalManagement.enums.AppointmentStatus;
import com.HospitalManagement.enums.Roles;
import com.HospitalManagement.repository.AppointmentRepository;
import com.HospitalManagement.repository.PatientRepository;
import com.HospitalManagement.repository.UserRepository;
import com.HospitalManagement.requestdto.AppointmentRequestDto;
import com.HospitalManagement.responsedto.AppointmentResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Appointment Service Tests")
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    private Patient patient;
    private User clinician;
    private User creator;
    private Appointment appointment;
    private AppointmentRequestDto requestDto;
    private LocalDateTime start;
    private LocalDateTime end;

    @BeforeEach
    void setUp() {
        start = LocalDateTime.now().plusDays(1);
        end = start.plusMinutes(30);

        clinician = User.builder()
                .userId(10L)
                .name("Dr. Asha Mehta")
                .role(Roles.CLINICIAN)
                .status("ACTIVE")
                .build();

        creator = User.builder()
                .userId(11L)
                .name("Rahul Receptionist")
                .role(Roles.RECEPTION)
                .status("ACTIVE")
                .build();

        patient = Patient.builder()
                .patientId(5L)
                .name("Priya Nair")
                .mrn("MRN-2026-001")
                .build();

        appointment = new Appointment();
        appointment.setApptId(1L);
        appointment.setPatient(patient);
        appointment.setClinician(clinician);
        appointment.setDepartment("General Medicine");
        appointment.setServiceType("Consultation");
        appointment.setStartAt(start);
        appointment.setEndAt(end);
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointment.setCreatedBy(creator);
        appointment.setCreatedAt(LocalDateTime.now());

        requestDto = new AppointmentRequestDto(
                5L,
                10L,
                "General Medicine",
                "Consultation",
                start,
                end,
                AppointmentStatus.SCHEDULED,
                11L
        );
    }

    @Test
    @DisplayName("Should retrieve all appointments successfully")
    void testGetAllAppointments() {
        // Arrange
        when(appointmentRepository.findAll()).thenReturn(Arrays.asList(appointment));

        // Act
        List<AppointmentResponseDto> result = appointmentService.getAllAppointments();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Priya Nair", result.get(0).patientName());
        assertEquals("Dr. Asha Mehta", result.get(0).clinicianName());
        verify(appointmentRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should retrieve appointment by ID successfully")
    void testGetAppointmentById() {
        // Arrange
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        // Act
        AppointmentResponseDto result = appointmentService.getAppointmentById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.apptId());
        assertEquals(5L, result.patientId());
        verify(appointmentRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when appointment not found by ID")
    void testGetAppointmentByIdNotFound() {
        // Arrange
        when(appointmentRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> appointmentService.getAppointmentById(999L));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    @DisplayName("Should retrieve appointments by patient ID successfully")
    void testGetAppointmentsByPatient() {
        // Arrange
        when(appointmentRepository.findByPatientPatientIdOrderByStartAtDesc(5L)).thenReturn(Arrays.asList(appointment));

        // Act
        List<AppointmentResponseDto> result = appointmentService.getAppointmentsByPatient(5L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(appointmentRepository, times(1)).findByPatientPatientIdOrderByStartAtDesc(5L);
    }

    @Test
    @DisplayName("Should create appointment successfully when clinician is available")
    void testCreateAppointmentSuccess() {
        // Arrange
        when(patientRepository.findById(5L)).thenReturn(Optional.of(patient));
        when(userRepository.findById(10L)).thenReturn(Optional.of(clinician));
        when(userRepository.findById(11L)).thenReturn(Optional.of(creator));
        when(appointmentRepository.existsByClinicianUserId(eq(10L), eq(end), eq(start), anyList())).thenReturn(false);
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        // Act
        AppointmentResponseDto result = appointmentService.createAppointment(requestDto);

        // Assert
        assertNotNull(result);
        assertEquals("General Medicine", result.department());
        assertEquals(AppointmentStatus.SCHEDULED, result.status());
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }

    @Test
    @DisplayName("Should throw exception when creating appointment and clinician is double-booked")
    void testCreateAppointmentClinicianOverlap() {
        // Arrange
        when(appointmentRepository.existsByClinicianUserId(eq(10L), eq(end), eq(start), anyList())).thenReturn(true);

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> appointmentService.createAppointment(requestDto));
        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    @DisplayName("Should throw exception when appointment time range is invalid")
    void testCreateAppointmentInvalidTimeRange() {
        // Arrange
        AppointmentRequestDto invalidRequest = new AppointmentRequestDto(
                5L, 10L, "General Medicine", "Consultation", end, start, AppointmentStatus.SCHEDULED, 11L
        );

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> appointmentService.createAppointment(invalidRequest));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    @DisplayName("Should update appointment successfully")
    void testUpdateAppointment() {
        // Arrange
        Appointment existingAppointment = new Appointment();
        existingAppointment.setApptId(1L);
        existingAppointment.setPatient(patient);
        existingAppointment.setClinician(clinician);
        existingAppointment.setStartAt(start);
        existingAppointment.setEndAt(end);
        existingAppointment.setStatus(AppointmentStatus.SCHEDULED);

        AppointmentRequestDto updateRequest = new AppointmentRequestDto(
                5L,
                10L,
                "Cardiology",
                "Follow Up",
                start.plusHours(1),
                end.plusHours(1),
                AppointmentStatus.SCHEDULED,
                11L
        );

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(existingAppointment));
        when(patientRepository.findById(5L)).thenReturn(Optional.of(patient));
        when(userRepository.findById(10L)).thenReturn(Optional.of(clinician));
        when(userRepository.findById(11L)).thenReturn(Optional.of(creator));
        // Clinician should be available for the new slot
        when(appointmentRepository.existsByClinicianUserIdAndStartAtLessThanAndEndAtGreaterThanAndStatusNotInAndApptIdNot(
                eq(10L), any(LocalDateTime.class), any(LocalDateTime.class), anyList(), eq(1L)
        )).thenReturn(false);
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(existingAppointment);

        // Act
        AppointmentResponseDto result = appointmentService.updateAppointment(1L, updateRequest);

        // Assert
        assertNotNull(result);
        assertEquals("Cardiology", result.department());
        assertEquals("Follow Up", result.serviceType());
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }

    @Test
    @DisplayName("Should check in appointment successfully")
    void testCheckInAppointment() {
        // Arrange
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        // Act
        AppointmentResponseDto result = appointmentService.checkInAppointment(1L);

        // Assert
        assertNotNull(result);
        assertEquals(AppointmentStatus.CHECKED_IN, result.status());
        verify(appointmentRepository, times(1)).save(appointment);
    }

    @Test
    @DisplayName("Should complete appointment successfully")
    void testCompleteAppointment() {
        // Arrange
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        // Act
        AppointmentResponseDto result = appointmentService.completeAppointment(1L);

        // Assert
        assertNotNull(result);
        assertEquals(AppointmentStatus.COMPLETED, result.status());
        verify(appointmentRepository, times(1)).save(appointment);
    }

    @Test
    @DisplayName("Should cancel appointment successfully")
    void testCancelAppointment() {
        // Arrange
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        // Act
        AppointmentResponseDto result = appointmentService.cancelAppointment(1L);

        // Assert
        assertNotNull(result);
        assertEquals(AppointmentStatus.CANCELLED, result.status());
        verify(appointmentRepository, times(1)).save(appointment);
    }
}

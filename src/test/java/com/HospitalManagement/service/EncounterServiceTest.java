package com.HospitalManagement.service;

import com.HospitalManagement.entity.Encounter;
import com.HospitalManagement.entity.Patient;
import com.HospitalManagement.entity.User;
import com.HospitalManagement.enums.EncounterStatus;
import com.HospitalManagement.enums.Roles;
import com.HospitalManagement.repository.EncounterRepository;
import com.HospitalManagement.repository.PatientRepository;
import com.HospitalManagement.repository.UserRepository;
import com.HospitalManagement.repository.PrescriptionRepository;
import com.HospitalManagement.requestdto.EncounterRequestDto;
import com.HospitalManagement.responsedto.EncounterResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Encounter Service Tests")
class EncounterServiceTest {

    @Mock
    private EncounterRepository encounterRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PrescriptionRepository prescriptionRepository;

    @InjectMocks
    private EncounterService encounterService;

    private Encounter encounter;
    private EncounterRequestDto requestDto;
    private Patient patient;
    private User clinician;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();

        // Setup mock clinician
        clinician = User.builder()
                .userId(1L)
                .name("Dr. Smith")
                .email("dr.smith@hospital.com")
                .role(Roles.CLINICIAN)
                .status("ACTIVE")
                .build();

        patient = Patient.builder()
                .patientId(1L)
                .name("John Doe")
                .build();

        encounter = new Encounter();
        encounter.setEncounterId(1L);
        encounter.setPatient(patient);
        encounter.setClinician(clinician);
        encounter.setVisitType("Routine Checkup");
        encounter.setChiefComplaint("Headache");
        encounter.setVitalsJson("{\"temp\": 37.5}");
        encounter.setNotesJson("{\"notes\": \"Patient seems fine\"}");
        encounter.setDiagnosesJson("{\"diagnosis\": \"Common Cold\"}");
        encounter.setOrdersJson("{\"orders\": []}");
        encounter.setStartAt(now);
        encounter.setStatus(EncounterStatus.IN_PROGRESS);

        requestDto = new EncounterRequestDto(
                1L,
                "Routine Checkup",
                "Headache",
                "{\"temp\": 37.5}",
                "{\"notes\": \"Patient seems fine\"}",
                "{\"diagnosis\": \"Common Cold\"}",
                "{\"orders\": []}",
                EncounterStatus.IN_PROGRESS
        );

        // Setup security context
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken("dr.smith@hospital.com", null);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    @DisplayName("Should retrieve all encounters successfully")
    void testGetAllEncounters() {
        // Arrange
        List<Encounter> encounters = Arrays.asList(encounter);
        when(encounterRepository.findAll()).thenReturn(encounters);

        // Act
        List<EncounterResponseDto> result = encounterService.getAllEncounters();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Routine Checkup", result.get(0).visitType());
        verify(encounterRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should retrieve encounter by ID successfully")
    void testGetEncounterById() {
        // Arrange
        when(encounterRepository.findById(1L)).thenReturn(Optional.of(encounter));

        // Act
        EncounterResponseDto result = encounterService.getEncounterById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.encounterId());
        assertEquals("John Doe", result.patientName());
        assertEquals("Dr. Smith", result.clinicianName());
        verify(encounterRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when encounter not found by ID")
    void testGetEncounterByIdNotFound() {
        // Arrange
        when(encounterRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> encounterService.getEncounterById(999L));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    @DisplayName("Should create encounter successfully")
    void testCreateEncounter() {
        // Arrange
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(userRepository.findByEmail("dr.smith@hospital.com")).thenReturn(Optional.of(clinician));
        when(encounterRepository.save(any(Encounter.class))).thenReturn(encounter);

        // Act
        EncounterResponseDto result = encounterService.createEncounter(requestDto);

        // Assert
        assertNotNull(result);
        assertEquals("Routine Checkup", result.visitType());
        assertEquals(EncounterStatus.IN_PROGRESS, result.status());
        verify(encounterRepository, times(1)).save(any(Encounter.class));
    }

    @Test
    @DisplayName("Should throw exception when creating encounter with non-existent patient")
    void testCreateEncounterPatientNotFound() {
        // Arrange
        when(userRepository.findByEmail("dr.smith@hospital.com")).thenReturn(Optional.of(clinician));
        when(patientRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> encounterService.createEncounter(requestDto));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    @DisplayName("Should throw exception when non-clinician tries to create encounter")
    void testCreateEncounterNonClinicianForbidden() {
        // Arrange
        User nonClinician = User.builder()
                .userId(2L)
                .name("John Patient")
                .email("patient@hospital.com")
                .role(Roles.PATIENT)
                .status("ACTIVE")
                .build();

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken("patient@hospital.com", null);
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(userRepository.findByEmail("patient@hospital.com")).thenReturn(Optional.of(nonClinician));

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> encounterService.createEncounter(requestDto));
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
    }

    @Test
    @DisplayName("Should update encounter successfully")
    void testUpdateEncounter() {
        // Arrange
        Encounter existingEncounter = new Encounter();
        existingEncounter.setEncounterId(1L);
        existingEncounter.setPatient(patient);
        existingEncounter.setClinician(clinician);
        existingEncounter.setVisitType("Routine Checkup");
        existingEncounter.setStatus(EncounterStatus.IN_PROGRESS);

        EncounterRequestDto updateDto = new EncounterRequestDto(
                1L, "Follow-up Visit", "Recovery check", "{}", "{}", "{}", "{}", EncounterStatus.IN_PROGRESS
        );

        when(encounterRepository.findById(1L)).thenReturn(Optional.of(existingEncounter));
        when(userRepository.findByEmail("dr.smith@hospital.com")).thenReturn(Optional.of(clinician));
        when(encounterRepository.save(any(Encounter.class))).thenReturn(existingEncounter);

        // Act
        EncounterResponseDto result = encounterService.updateEncounter(1L, updateDto);

        // Assert
        assertNotNull(result);
        verify(encounterRepository, times(1)).save(any(Encounter.class));
    }

    @Test
    @DisplayName("Should throw exception when updating completed encounter")
    void testUpdateCompletedEncounterThrowsException() {
        // Arrange
        Encounter completedEncounter = new Encounter();
        completedEncounter.setEncounterId(1L);
        completedEncounter.setStatus(EncounterStatus.COMPLETED);

        when(encounterRepository.findById(1L)).thenReturn(Optional.of(completedEncounter));
        when(userRepository.findByEmail("dr.smith@hospital.com")).thenReturn(Optional.of(clinician));

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> encounterService.updateEncounter(1L, requestDto));
        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
    }

    @Test
    @DisplayName("Should complete encounter and set signed information")
    void testCompleteEncounter() {
        // Arrange
        Encounter inProgressEncounter = new Encounter();
        inProgressEncounter.setEncounterId(1L);
        inProgressEncounter.setPatient(patient);
        inProgressEncounter.setClinician(clinician);
        inProgressEncounter.setStatus(EncounterStatus.IN_PROGRESS);
        inProgressEncounter.setStartAt(now);

        EncounterRequestDto completeDto = new EncounterRequestDto(
                1L, "Routine", "Complaint", "{}", "{}", "{}", "{}", EncounterStatus.COMPLETED
        );

        when(encounterRepository.findById(1L)).thenReturn(Optional.of(inProgressEncounter));
        when(userRepository.findByEmail("dr.smith@hospital.com")).thenReturn(Optional.of(clinician));
        when(encounterRepository.save(any(Encounter.class))).thenAnswer(invocation -> {
            Encounter enc = invocation.getArgument(0);
            assertNotNull(enc.getEndAt());
            assertNotNull(enc.getSignedAt());
            assertNotNull(enc.getSignedBy());
            assertEquals(EncounterStatus.COMPLETED, enc.getStatus());
            return enc;
        });

        // Act
        EncounterResponseDto result = encounterService.updateEncounter(1L, completeDto);

        // Assert
        assertNotNull(result);
        verify(encounterRepository, times(1)).save(any(Encounter.class));
    }

    @Test
    @DisplayName("Should delete encounter successfully")
    void testDeleteEncounter() {
        // Arrange
        when(encounterRepository.findById(1L)).thenReturn(Optional.of(encounter));
        doNothing().when(encounterRepository).delete(any(Encounter.class));

        // Act
        encounterService.deleteEncounter(1L);

        // Assert
        verify(encounterRepository, times(1)).delete(any(Encounter.class));
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent encounter")
    void testDeleteEncounterNotFound() {
        // Arrange
        when(encounterRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> encounterService.deleteEncounter(999L));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    @DisplayName("Should map encounter to response DTO correctly")
    void testEncounterToResponseDtoMapping() {
        // Arrange
        when(encounterRepository.findById(1L)).thenReturn(Optional.of(encounter));

        // Act
        EncounterResponseDto result = encounterService.getEncounterById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.encounterId());
        assertEquals(1L, result.patientId());
        assertEquals("John Doe", result.patientName());
        assertEquals(1L, result.clinicianId());
        assertEquals("Dr. Smith", result.clinicianName());
        assertEquals("Routine Checkup", result.visitType());
        assertEquals("Headache", result.chiefComplaint());
        assertEquals(EncounterStatus.IN_PROGRESS, result.status());
    }

    @Test
    @DisplayName("Should return empty list when no encounters exist")
    void testGetAllEncountersEmpty() {
        // Arrange
        when(encounterRepository.findAll()).thenReturn(Arrays.asList());

        // Act
        List<EncounterResponseDto> result = encounterService.getAllEncounters();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(encounterRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should set encounter status to IN_PROGRESS on creation")
    void testEncounterStatusSetToInProgressOnCreation() {
        // Arrange
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(userRepository.findByEmail("dr.smith@hospital.com")).thenReturn(Optional.of(clinician));
        when(encounterRepository.save(any(Encounter.class))).thenAnswer(invocation -> {
            Encounter enc = invocation.getArgument(0);
            assertEquals(EncounterStatus.IN_PROGRESS, enc.getStatus());
            return enc;
        });

        // Act
        encounterService.createEncounter(requestDto);

        // Assert
        verify(encounterRepository, times(1)).save(argThat(e -> e.getStatus() == EncounterStatus.IN_PROGRESS));
    }

    @Test
    @DisplayName("Should preserve patient and clinician relationship")
    void testEncounterPatientClinicianRelationship() {
        // Arrange
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(userRepository.findByEmail("dr.smith@hospital.com")).thenReturn(Optional.of(clinician));
        when(encounterRepository.save(any(Encounter.class))).thenReturn(encounter);

        // Act
        EncounterResponseDto result = encounterService.createEncounter(requestDto);

        // Assert
        assertNotNull(result);
        assertEquals("John Doe", result.patientName());
        assertEquals("Dr. Smith", result.clinicianName());
    }

    @Test
    @DisplayName("Should preserve JSON data fields")
    void testEncounterPreservesJsonData() {
        // Arrange
        when(encounterRepository.findById(1L)).thenReturn(Optional.of(encounter));

        // Act
        EncounterResponseDto result = encounterService.getEncounterById(1L);

        // Assert
        assertNotNull(result);
        assertNotNull(result.vitalsJson());
        assertNotNull(result.notesJson());
        assertNotNull(result.diagnosesJson());
        assertEquals("{\"temp\": 37.5}", result.vitalsJson());
    }
}

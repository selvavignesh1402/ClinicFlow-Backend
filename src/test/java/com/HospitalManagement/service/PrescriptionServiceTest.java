package com.HospitalManagement.service;

import com.HospitalManagement.entity.*;
import com.HospitalManagement.enums.PrescriptionStatus;
import com.HospitalManagement.enums.Roles;
import com.HospitalManagement.repository.*;
import com.HospitalManagement.requestdto.PrescriptionRequestDto;
import com.HospitalManagement.responsedto.PrescriptionResponseDto;
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
@DisplayName("Prescription Service Tests")
class PrescriptionServiceTest {

    @Mock
    private PrescriptionRepository prescriptionRepository;

    @Mock
    private EncounterRepository encounterRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MedicationMasterRepository medicationMasterRepository;

    @InjectMocks
    private PrescriptionService prescriptionService;

    private Prescription prescription;
    private PrescriptionRequestDto requestDto;
    private Patient patient;
    private User clinician;
    private Encounter encounter;
    private MedicationMaster medication;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();

        // Setup mock user
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

        medication = MedicationMaster.builder()
                .medId(1L)
                .code("MED001")
                .name("Aspirin")
                .build();

        prescription = new Prescription();
        prescription.setRxId(1L);
        prescription.setPatient(patient);
        prescription.setClinician(clinician);
        prescription.setEncounter(encounter);
        prescription.setMedication(medication);
        prescription.setDosage("500mg");
        prescription.setFrequency("Twice daily");
        prescription.setDurationDays(10);
        prescription.setQuantity(20);
        prescription.setRepeats(2);
        prescription.setRoute("Oral");
        prescription.setNotes("After meals");
        prescription.setStatus(PrescriptionStatus.DRAFT);
        prescription.setIssuedAt(now);

        requestDto = new PrescriptionRequestDto(
                1L, 1L, 1L, "500mg", "Twice daily",
                10, 20, 2, "Oral", "After meals", PrescriptionStatus.DRAFT
        );

        // Setup security context
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken("dr.smith@hospital.com", null);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    @DisplayName("Should retrieve all prescriptions successfully")
    void testGetAllPrescriptions() {
        // Arrange
        List<Prescription> prescriptions = Arrays.asList(prescription);
        when(prescriptionRepository.findAll()).thenReturn(prescriptions);

        // Act
        List<PrescriptionResponseDto> result = prescriptionService.getAllPrescriptions();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("500mg", result.get(0).dosage());
        verify(prescriptionRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should retrieve prescription by ID successfully")
    void testGetPrescriptionById() {
        // Arrange
        when(prescriptionRepository.findById(1L)).thenReturn(Optional.of(prescription));

        // Act
        PrescriptionResponseDto result = prescriptionService.getPrescriptionById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.rxId());
        assertEquals("Aspirin", result.medicationName());
        verify(prescriptionRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when prescription not found by ID")
    void testGetPrescriptionByIdNotFound() {
        // Arrange
        when(prescriptionRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> prescriptionService.getPrescriptionById(999L));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    @DisplayName("Should create prescription successfully")
    void testCreatePrescription() {
        // Arrange
        when(encounterRepository.findById(1L)).thenReturn(Optional.of(encounter));
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(medicationMasterRepository.findById(1L)).thenReturn(Optional.of(medication));
        when(userRepository.findByEmail("dr.smith@hospital.com")).thenReturn(Optional.of(clinician));
        when(prescriptionRepository.save(any(Prescription.class))).thenReturn(prescription);

        // Act
        PrescriptionResponseDto result = prescriptionService.createPrescription(requestDto);

        // Assert
        assertNotNull(result);
        assertEquals("Aspirin", result.medicationName());
        assertEquals(PrescriptionStatus.DRAFT, result.status());
        verify(prescriptionRepository, times(1)).save(any(Prescription.class));
    }

    @Test
    @DisplayName("Should throw exception when creating prescription with non-existent encounter")
    void testCreatePrescriptionEncounterNotFound() {
        // Arrange
        when(encounterRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> prescriptionService.createPrescription(requestDto));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    @DisplayName("Should throw exception when creating prescription with non-existent patient")
    void testCreatePrescriptionPatientNotFound() {
        // Arrange
        when(encounterRepository.findById(1L)).thenReturn(Optional.of(encounter));
        when(patientRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> prescriptionService.createPrescription(requestDto));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    @DisplayName("Should throw exception when creating prescription with non-existent medication")
    void testCreatePrescriptionMedicationNotFound() {
        // Arrange
        when(encounterRepository.findById(1L)).thenReturn(Optional.of(encounter));
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(medicationMasterRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> prescriptionService.createPrescription(requestDto));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    @DisplayName("Should update prescription status successfully")
    void testUpdatePrescriptionStatus() {
        // Arrange
        Prescription draftPrescription = new Prescription();
        draftPrescription.setRxId(1L);
        draftPrescription.setPatient(patient);
        draftPrescription.setClinician(clinician);
        draftPrescription.setEncounter(encounter);
        draftPrescription.setMedication(medication);
        draftPrescription.setDosage("500mg");
        draftPrescription.setStatus(PrescriptionStatus.DRAFT);

        PrescriptionRequestDto updateDto = new PrescriptionRequestDto(
                1L, 1L, 1L, "500mg", "Twice daily",
                10, 20, 2, "Oral", "After meals", PrescriptionStatus.ISSUED
        );

        when(prescriptionRepository.findById(1L)).thenReturn(Optional.of(draftPrescription));
        when(userRepository.findByEmail("dr.smith@hospital.com")).thenReturn(Optional.of(clinician));
        when(prescriptionRepository.save(any(Prescription.class))).thenReturn(draftPrescription);

        // Act
        PrescriptionResponseDto result = prescriptionService.updatePrescription(1L, updateDto);

        // Assert
        assertNotNull(result);
        verify(prescriptionRepository, times(1)).save(any(Prescription.class));
    }

    @Test
    @DisplayName("Should throw exception when updating issued prescription")
    void testUpdateIssuedPrescriptionThrowsException() {
        // Arrange
        Prescription issuedPrescription = new Prescription();
        issuedPrescription.setRxId(1L);
        issuedPrescription.setStatus(PrescriptionStatus.ISSUED);

        PrescriptionRequestDto updateDto = new PrescriptionRequestDto(
                1L, 1L, 1L, "1000mg", "Three times daily",
                15, 30, 3, "Oral", "After meals", PrescriptionStatus.ISSUED
        );

        when(prescriptionRepository.findById(1L)).thenReturn(Optional.of(issuedPrescription));

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> prescriptionService.updatePrescription(1L, updateDto));
        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
    }

    @Test
    @DisplayName("Should delete prescription successfully")
    void testDeletePrescription() {
        // Arrange
        when(prescriptionRepository.findById(1L)).thenReturn(Optional.of(prescription));
        doNothing().when(prescriptionRepository).delete(any(Prescription.class));

        // Act
        prescriptionService.deletePrescription(1L);

        // Assert
        verify(prescriptionRepository, times(1)).delete(any(Prescription.class));
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent prescription")
    void testDeletePrescriptionNotFound() {
        // Arrange
        when(prescriptionRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> prescriptionService.deletePrescription(999L));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    @DisplayName("Should map prescription to response DTO correctly")
    void testPrescriptionToResponseDtoMapping() {
        // Arrange
        when(prescriptionRepository.findById(1L)).thenReturn(Optional.of(prescription));

        // Act
        PrescriptionResponseDto result = prescriptionService.getPrescriptionById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.rxId());
        assertEquals("John Doe", result.patientName());
        assertEquals("Dr. Smith", result.clinicianName());
        assertEquals("Aspirin", result.medicationName());
        assertEquals("500mg", result.dosage());
        assertEquals("Twice daily", result.frequency());
        assertEquals(10, result.durationDays());
        assertEquals(20, result.quantity());
        assertEquals(2, result.repeats());
        assertEquals("Oral", result.route());
        assertEquals("After meals", result.notes());
    }

    @Test
    @DisplayName("Should set clinician from authenticated user")
    void testClinicianSetFromAuthentication() {
        // Arrange
        when(encounterRepository.findById(1L)).thenReturn(Optional.of(encounter));
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(medicationMasterRepository.findById(1L)).thenReturn(Optional.of(medication));
        when(userRepository.findByEmail("dr.smith@hospital.com")).thenReturn(Optional.of(clinician));
        when(prescriptionRepository.save(any(Prescription.class))).thenReturn(prescription);

        // Act
        prescriptionService.createPrescription(requestDto);

        // Assert
        verify(prescriptionRepository, times(1)).save(argThat(p ->
                p.getClinician().getEmail().equals("dr.smith@hospital.com")
        ));
    }

    @Test
    @DisplayName("Should return empty list when no prescriptions exist")
    void testGetAllPrescriptionsEmpty() {
        // Arrange
        when(prescriptionRepository.findAll()).thenReturn(Arrays.asList());

        // Act
        List<PrescriptionResponseDto> result = prescriptionService.getAllPrescriptions();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(prescriptionRepository, times(1)).findAll();
    }
}

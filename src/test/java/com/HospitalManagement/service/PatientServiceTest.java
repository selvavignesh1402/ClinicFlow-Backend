package com.HospitalManagement.service;

import com.HospitalManagement.entity.Patient;
import com.HospitalManagement.repository.PatientRepository;
import com.HospitalManagement.requestdto.PatientRequestDto;
import com.HospitalManagement.responsedto.PatientResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Patient Service Tests")
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientService patientService;

    private Patient patient;
    private PatientRequestDto requestDto;
    private LocalDate dob;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        dob = LocalDate.of(1990, 1, 1);
        now = LocalDateTime.now();

        patient = new Patient();
        patient.setPatientId(1L);
        patient.setMrn("MRN-ABC12345");
        patient.setName("Jane Doe");
        patient.setDob(dob);
        patient.setGender("FEMALE");
        patient.setContactInfoJson("{\"phone\":\"9876543210\",\"email\":\"jane.doe@example.com\"}");
        patient.setAddressJson("{\"line1\":\"123 Street\",\"city\":\"Bangalore\"}");
        patient.setPrimaryContact("John Doe");
        patient.setInsuranceId("INS001");
        patient.setStatus("ACTIVE");
        patient.setCreatedAt(now);
        patient.setUpdatedAt(now);
        patient.setVersion(1L);

        requestDto = new PatientRequestDto(
                "Jane Doe",
                dob,
                "FEMALE",
                "{\"phone\":\"9876543210\",\"email\":\"jane.doe@example.com\"}",
                "{\"line1\":\"123 Street\",\"city\":\"Bangalore\"}",
                "John Doe",
                "INS001",
                "ACTIVE"
        );
    }

    @Test
    @DisplayName("Should retrieve all patients successfully")
    void testGetAllPatients() {
        // Arrange
        when(patientRepository.findAll()).thenReturn(Arrays.asList(patient));

        // Act
        List<PatientResponseDto> result = patientService.getAllPatients();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Jane Doe", result.get(0).name());
        assertEquals("MRN-ABC12345", result.get(0).mrn());
        verify(patientRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should retrieve patient by ID successfully")
    void testGetPatientById() {
        // Arrange
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));

        // Act
        PatientResponseDto result = patientService.getPatientById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.patientId());
        assertEquals("Jane Doe", result.name());
        verify(patientRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when patient not found by ID")
    void testGetPatientByIdNotFound() {
        // Arrange
        when(patientRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> patientService.getPatientById(999L));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    @DisplayName("Should retrieve patient by MRN successfully")
    void testGetPatientByMrn() {
        // Arrange
        when(patientRepository.findByMrn("MRN-ABC12345")).thenReturn(Optional.of(patient));

        // Act
        PatientResponseDto result = patientService.getPatientByMrn("MRN-ABC12345");

        // Assert
        assertNotNull(result);
        assertEquals("Jane Doe", result.name());
        verify(patientRepository, times(1)).findByMrn("MRN-ABC12345");
    }

    @Test
    @DisplayName("Should throw exception when patient not found by MRN")
    void testGetPatientByMrnNotFound() {
        // Arrange
        when(patientRepository.findByMrn("MRN-NOTFOUND")).thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> patientService.getPatientByMrn("MRN-NOTFOUND"));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    @DisplayName("Should register patient successfully and auto-generate MRN")
    void testRegisterPatient() {
        // Arrange
        when(patientRepository.existsByMrn(anyString())).thenReturn(false);
        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> {
            Patient p = invocation.getArgument(0);
            p.setPatientId(2L);
            return p;
        });

        // Act
        PatientResponseDto result = patientService.registerPatient(requestDto);

        // Assert
        assertNotNull(result);
        assertEquals(2L, result.patientId());
        assertEquals("Jane Doe", result.name());
        assertTrue(result.mrn().startsWith("MRN-"));
        assertEquals(12, result.mrn().length()); // MRN- + 8 alphanumeric characters
        verify(patientRepository, times(1)).save(any(Patient.class));
    }

    @Test
    @DisplayName("Should update patient successfully")
    void testUpdatePatient() {
        // Arrange
        Patient existingPatient = new Patient();
        existingPatient.setPatientId(1L);
        existingPatient.setMrn("MRN-ABC12345");
        existingPatient.setName("Old Name");
        existingPatient.setStatus("ACTIVE");

        PatientRequestDto updateRequest = new PatientRequestDto(
                "Updated Name",
                dob,
                "FEMALE",
                "{}",
                "{}",
                "John Doe",
                "INS001",
                "ACTIVE"
        );

        when(patientRepository.findById(1L)).thenReturn(Optional.of(existingPatient));
        when(patientRepository.save(any(Patient.class))).thenReturn(existingPatient);

        // Act
        PatientResponseDto result = patientService.updatePatient(1L, updateRequest);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Name", result.name());
        assertEquals("MRN-ABC12345", result.mrn());
        verify(patientRepository, times(1)).save(any(Patient.class));
    }

    @Test
    @DisplayName("Should deactivate patient successfully")
    void testDeactivatePatient() {
        // Arrange
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        // Act
        patientService.deactivatePatient(1L);

        // Assert
        assertEquals("INACTIVE", patient.getStatus());
        verify(patientRepository, times(1)).save(patient);
    }
}

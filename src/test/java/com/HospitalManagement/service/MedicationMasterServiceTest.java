package com.HospitalManagement.service;

import com.HospitalManagement.entity.MedicationMaster;
import com.HospitalManagement.enums.MedicationStatus;
import com.HospitalManagement.repository.MedicationMasterRepository;
import com.HospitalManagement.requestdto.PharmacyRequestDto;
import com.HospitalManagement.responsedto.MedicationResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Medication Master Service Tests")
class MedicationMasterServiceTest {

    @Mock
    private MedicationMasterRepository medicationRepository;

    @InjectMocks
    private MedicationMasterService medicationService;

    private MedicationMaster medication;
    private PharmacyRequestDto requestDto;

    @BeforeEach
    void setUp() {
        medication = MedicationMaster.builder()
                .medId(1L)
                .code("MED001")
                .name("Paracetamol")
                .formulation("Tablet")
                .strength("500mg")
                .atcCode("N02BE01")
                .controlledFlag(false)
                .status(MedicationStatus.ACTIVE)
                .build();

        requestDto = new PharmacyRequestDto(
                null, "MED001", "Paracetamol", "Tablet", "500mg", "N02BE01", false,
                null, null, null, null, null, null, null, null, null, null, null, null
        );
    }

    @Test
    @DisplayName("Should retrieve all medications without search term successfully")
    void testGetAllMedicationsNoSearch() {
        // Arrange
        when(medicationRepository.findAll()).thenReturn(Arrays.asList(medication));

        // Act
        List<MedicationResponseDto> result = medicationService.getAllMedications(null);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Paracetamol", result.get(0).name());
        verify(medicationRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should retrieve medications with active search successfully")
    void testGetAllMedicationsWithSearch() {
        // Arrange
        when(medicationRepository.findByNameContainingIgnoreCaseOrCodeContainingIgnoreCase("Para", "Para"))
                .thenReturn(Arrays.asList(medication));

        // Act
        List<MedicationResponseDto> result = medicationService.getAllMedications("Para");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(medicationRepository, times(1)).findByNameContainingIgnoreCaseOrCodeContainingIgnoreCase("Para", "Para");
    }

    @Test
    @DisplayName("Should retrieve medication by ID successfully")
    void testGetMedicationById() {
        // Arrange
        when(medicationRepository.findById(1L)).thenReturn(Optional.of(medication));

        // Act
        MedicationResponseDto result = medicationService.getMedicationById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.medId());
        assertEquals("Paracetamol", result.name());
    }

    @Test
    @DisplayName("Should create medication successfully and default status to ACTIVE")
    void testCreateMedicationSuccess() {
        // Arrange
        when(medicationRepository.existsByCode("MED001")).thenReturn(false);
        when(medicationRepository.save(any(MedicationMaster.class))).thenReturn(medication);

        // Act
        MedicationResponseDto result = medicationService.createMedication(requestDto);

        // Assert
        assertNotNull(result);
        assertEquals("MED001", result.code());
        verify(medicationRepository, times(1)).save(any(MedicationMaster.class));
    }

    @Test
    @DisplayName("Should throw exception when creating medication with existing code")
    void testCreateMedicationDuplicateCode() {
        // Arrange
        when(medicationRepository.existsByCode("MED001")).thenReturn(true);

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> medicationService.createMedication(requestDto));
        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        verify(medicationRepository, never()).save(any(MedicationMaster.class));
    }

    @Test
    @DisplayName("Should update medication details successfully")
    void testUpdateMedicationSuccess() {
        // Arrange
        when(medicationRepository.findById(1L)).thenReturn(Optional.of(medication));
        when(medicationRepository.findByCode("MED001")).thenReturn(Optional.of(medication));
        when(medicationRepository.save(any(MedicationMaster.class))).thenReturn(medication);

        // Act
        MedicationResponseDto result = medicationService.updateMedication(1L, requestDto);

        // Assert
        assertNotNull(result);
        verify(medicationRepository, times(1)).save(any(MedicationMaster.class));
    }

    @Test
    @DisplayName("Should delete (deactivate) medication successfully and mark status to INACTIVE")
    void testDeleteMedication() {
        // Arrange
        when(medicationRepository.findById(1L)).thenReturn(Optional.of(medication));
        when(medicationRepository.save(any(MedicationMaster.class))).thenReturn(medication);

        // Act
        medicationService.deleteMedication(1L);

        // Assert
        assertEquals(MedicationStatus.INACTIVE, medication.getStatus());
        verify(medicationRepository, times(1)).save(medication);
    }
}

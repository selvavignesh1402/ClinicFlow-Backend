package com.HospitalManagement.service;

import com.HospitalManagement.entity.*;
import com.HospitalManagement.enums.DispenseStatus;
import com.HospitalManagement.enums.InventoryStatus;
import com.HospitalManagement.enums.MedicationStatus;
import com.HospitalManagement.enums.PrescriptionStatus;
import com.HospitalManagement.repository.DispenseRecordRepository;
import com.HospitalManagement.repository.InventoryItemRepository;
import com.HospitalManagement.repository.PrescriptionRepository;
import com.HospitalManagement.repository.UserRepository;
import com.HospitalManagement.requestdto.PharmacyRequestDto;
import com.HospitalManagement.responsedto.DispenseResponseDto;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pharmacy Service Tests")
class PharmacyServiceTest {

    @Mock
    private DispenseRecordRepository dispenseRepository;

    @Mock
    private PrescriptionRepository prescriptionRepository;

    @Mock
    private InventoryItemRepository inventoryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private InventoryService inventoryService;

    @InjectMocks
    private PharmacyService pharmacyService;

    private Patient patient;
    private User pharmacist;
    private MedicationMaster medication;
    private Prescription prescription;
    private InventoryItem inventoryItem;
    private DispenseRecord dispenseRecord;
    private PharmacyRequestDto dispenseRequestDto;

    @BeforeEach
    void setUp() {
        patient = Patient.builder().patientId(1L).name("Priya Nair").build();
        pharmacist = User.builder().userId(20L).name("Joe Pharmacist").build();

        medication = MedicationMaster.builder()
                .medId(5L)
                .code("MED001")
                .name("Paracetamol")
                .status(MedicationStatus.ACTIVE)
                .build();

        prescription = new Prescription();
        prescription.setRxId(10L);
        prescription.setPatient(patient);
        prescription.setMedication(medication);
        prescription.setQuantity(20);
        prescription.setStatus(PrescriptionStatus.ISSUED);

        inventoryItem = new InventoryItem();
        inventoryItem.setInventoryId(100L);
        inventoryItem.setMedication(medication);
        inventoryItem.setBatchNumber("BATCH-A");
        inventoryItem.setQuantity(100);
        inventoryItem.setExpiryDate(LocalDate.now().plusMonths(6));
        inventoryItem.setStatus(InventoryStatus.IN_STOCK);

        dispenseRecord = new DispenseRecord();
        dispenseRecord.setDispenseId(200L);
        dispenseRecord.setPrescription(prescription);
        dispenseRecord.setInventoryItem(inventoryItem);
        dispenseRecord.setPatient(patient);
        dispenseRecord.setDispensedBy(pharmacist);
        dispenseRecord.setQuantity(10);
        dispenseRecord.setStatus(DispenseStatus.DISPENSED);

        dispenseRequestDto = new PharmacyRequestDto(
                5L, null, null, null, null, null, null,
                "BATCH-A", 10, "Tablet", null, null, null, null,
                10L, 100L, 20L, null, "Take after food"
        );
    }

    @Test
    @DisplayName("Should retrieve all dispense records successfully")
    void testGetAllDispenseRecords() {
        // Arrange
        when(dispenseRepository.findAll()).thenReturn(Arrays.asList(dispenseRecord));

        // Act
        List<DispenseResponseDto> result = pharmacyService.getAllDispenseRecords();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(10, result.get(0).quantity());
        verify(dispenseRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should dispense prescription successfully using standard batch validations")
    void testDispensePrescriptionSuccess() {
        // Arrange
        when(prescriptionRepository.findById(10L)).thenReturn(Optional.of(prescription));
        when(userRepository.findById(20L)).thenReturn(Optional.of(pharmacist));
        when(inventoryService.findInventoryItem(100L)).thenReturn(inventoryItem);
        when(inventoryService.isDispensableBatch(inventoryItem)).thenReturn(true);
        when(dispenseRepository.save(any(DispenseRecord.class))).thenReturn(dispenseRecord);

        // Act
        DispenseResponseDto result = pharmacyService.dispensePrescription(dispenseRequestDto);

        // Assert
        assertNotNull(result);
        assertEquals(90, inventoryItem.getQuantity()); // 100 - 10
        assertEquals(PrescriptionStatus.DISPENSED, prescription.getStatus());
        verify(inventoryRepository, times(1)).save(inventoryItem);
        verify(dispenseRepository, times(1)).save(any(DispenseRecord.class));
    }

    @Test
    @DisplayName("Should throw exception when dispensing prescription not in ISSUED status")
    void testDispensePrescriptionNotIssuedThrowsException() {
        // Arrange
        prescription.setStatus(PrescriptionStatus.DRAFT);
        when(prescriptionRepository.findById(10L)).thenReturn(Optional.of(prescription));
        when(userRepository.findById(20L)).thenReturn(Optional.of(pharmacist));
        when(inventoryService.findInventoryItem(100L)).thenReturn(inventoryItem);

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> pharmacyService.dispensePrescription(dispenseRequestDto));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Prescription is not issued", exception.getReason());
    }

    @Test
    @DisplayName("Should throw exception when dispense quantity exceeds prescription quantity")
    void testDispensePrescriptionQuantityExceededThrowsException() {
        // Arrange
        PharmacyRequestDto highQtyRequest = new PharmacyRequestDto(
                5L, null, null, null, null, null, null, "BATCH-A", 30, null, null, null,
                null, null, 10L, 100L, 20L, null, null
        );

        when(prescriptionRepository.findById(10L)).thenReturn(Optional.of(prescription));
        when(userRepository.findById(20L)).thenReturn(Optional.of(pharmacist));
        when(inventoryService.findInventoryItem(100L)).thenReturn(inventoryItem);

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> pharmacyService.dispensePrescription(highQtyRequest));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Quantity is more than prescription quantity", exception.getReason());
    }

    @Test
    @DisplayName("Should process return of dispense successfully and add items back to inventory")
    void testReturnDispenseSuccess() {
        // Arrange
        PharmacyRequestDto returnDto = new PharmacyRequestDto(
                null, null, null, null, null, null, null, null, 10, null, null, null,
                null, null, null, null, null, null, null
        );

        when(dispenseRepository.findById(200L)).thenReturn(Optional.of(dispenseRecord));
        when(dispenseRepository.save(any(DispenseRecord.class))).thenReturn(dispenseRecord);

        // Act
        DispenseResponseDto result = pharmacyService.returnDispense(200L, returnDto);

        // Assert
        assertNotNull(result);
        assertEquals(110, inventoryItem.getQuantity()); // 100 + 10 returned
        verify(inventoryRepository, times(1)).save(inventoryItem);
    }
}

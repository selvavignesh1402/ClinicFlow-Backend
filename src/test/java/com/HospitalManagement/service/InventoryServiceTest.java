package com.HospitalManagement.service;

import com.HospitalManagement.entity.InventoryItem;
import com.HospitalManagement.entity.MedicationMaster;
import com.HospitalManagement.enums.InventoryStatus;
import com.HospitalManagement.enums.MedicationStatus;
import com.HospitalManagement.repository.InventoryItemRepository;
import com.HospitalManagement.repository.MedicationMasterRepository;
import com.HospitalManagement.requestdto.PharmacyRequestDto;
import com.HospitalManagement.responsedto.InventoryResponseDto;
import com.HospitalManagement.responsedto.StockSummaryResponseDto;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Inventory Service Tests")
class InventoryServiceTest {

    @Mock
    private InventoryItemRepository inventoryRepository;

    @Mock
    private MedicationMasterRepository medicationRepository;

    @InjectMocks
    private InventoryService inventoryService;

    private MedicationMaster medication;
    private InventoryItem item;
    private PharmacyRequestDto requestDto;
    private LocalDate futureExpiry;

    @BeforeEach
    void setUp() {
        futureExpiry = LocalDate.now().plusMonths(6);

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

        item = new InventoryItem();
        item.setInventoryId(10L);
        item.setMedication(medication);
        item.setBatchNumber("BATCH123");
        item.setQuantity(100);
        item.setUnit("Tablet");
        item.setExpiryDate(futureExpiry);
        item.setLocation("Aisle 3 Shelf A");
        item.setCostPrice(1.50);
        item.setStatus(InventoryStatus.IN_STOCK);

        requestDto = new PharmacyRequestDto(
                1L, null, null, null, null, null, null,
                "BATCH123", 100, "Tablet", futureExpiry, "Aisle 3 Shelf A",
                1.50, InventoryStatus.IN_STOCK, null, null, null, null, null
        );
    }

    @Test
    @DisplayName("Should retrieve all inventory successfully")
    void testGetAllInventory() {
        // Arrange
        when(inventoryRepository.findAll()).thenReturn(Arrays.asList(item));

        // Act
        List<InventoryResponseDto> result = inventoryService.getAllInventory();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("BATCH123", result.get(0).batchNumber());
        verify(inventoryRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should retrieve inventory item by ID successfully")
    void testGetInventoryItemById() {
        // Arrange
        when(inventoryRepository.findById(10L)).thenReturn(Optional.of(item));

        // Act
        InventoryResponseDto result = inventoryService.getInventoryItemById(10L);

        // Assert
        assertNotNull(result);
        assertEquals(10L, result.inventoryId());
        assertEquals("Paracetamol", result.medicationName());
        verify(inventoryRepository, times(1)).findById(10L);
    }

    @Test
    @DisplayName("Should throw exception when inventory item not found by ID")
    void testGetInventoryItemByIdNotFound() {
        // Arrange
        when(inventoryRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> inventoryService.getInventoryItemById(999L));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    @DisplayName("Should retrieve expiring inventory successfully")
    void testGetExpiringInventory() {
        // Arrange
        when(inventoryRepository.findByExpiryDateBetween(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Arrays.asList(item));

        // Act
        List<InventoryResponseDto> result = inventoryService.getExpiringInventory(30);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(inventoryRepository, times(1)).findByExpiryDateBetween(any(LocalDate.class), any(LocalDate.class));
    }

    @Test
    @DisplayName("Should retrieve expired inventory successfully")
    void testGetExpiredInventory() {
        // Arrange
        when(inventoryRepository.findByExpiryDateBefore(any(LocalDate.class))).thenReturn(Arrays.asList(item));

        // Act
        List<InventoryResponseDto> result = inventoryService.getExpiredInventory();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(inventoryRepository, times(1)).findByExpiryDateBefore(any(LocalDate.class));
    }

    @Test
    @DisplayName("Should generate stock summary correctly")
    void testGetStockSummary() {
        // Arrange
        when(inventoryRepository.findAll()).thenReturn(Arrays.asList(item));

        // Act
        List<StockSummaryResponseDto> result = inventoryService.getStockSummary();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Paracetamol", result.get(0).medicationName());
        assertEquals(100, result.get(0).totalQuantity());
    }

    @Test
    @DisplayName("Should create inventory item successfully")
    void testCreateInventoryItem() {
        // Arrange
        when(medicationRepository.findById(1L)).thenReturn(Optional.of(medication));
        when(inventoryRepository.findByMedicationAndBatchNumberIgnoreCase(medication, "BATCH123"))
                .thenReturn(Optional.empty());
        when(inventoryRepository.save(any(InventoryItem.class))).thenReturn(item);

        // Act
        InventoryResponseDto result = inventoryService.createInventoryItem(requestDto);

        // Assert
        assertNotNull(result);
        assertEquals("BATCH123", result.batchNumber());
        verify(inventoryRepository, times(1)).save(any(InventoryItem.class));
    }

    @Test
    @DisplayName("Should throw exception when creating inventory item with duplicate batch")
    void testCreateInventoryItemDuplicateBatch() {
        // Arrange
        when(medicationRepository.findById(1L)).thenReturn(Optional.of(medication));
        when(inventoryRepository.findByMedicationAndBatchNumberIgnoreCase(medication, "BATCH123"))
                .thenReturn(Optional.of(item));

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> inventoryService.createInventoryItem(requestDto));
        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        verify(inventoryRepository, never()).save(any(InventoryItem.class));
    }

    @Test
    @DisplayName("Should adjust stock delta successfully")
    void testAdjustStockSuccess() {
        // Arrange
        PharmacyRequestDto adjustDto = new PharmacyRequestDto(
                null, null, null, null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, -40, null
        );

        when(inventoryRepository.findById(10L)).thenReturn(Optional.of(item));
        when(inventoryRepository.save(any(InventoryItem.class))).thenReturn(item);

        // Act
        InventoryResponseDto result = inventoryService.adjustStock(10L, adjustDto);

        // Assert
        assertNotNull(result);
        assertEquals(60, item.getQuantity()); // 100 - 40
        verify(inventoryRepository, times(1)).save(item);
    }

    @Test
    @DisplayName("Should throw exception when adjustment results in negative stock")
    void testAdjustStockNegative() {
        // Arrange
        PharmacyRequestDto adjustDto = new PharmacyRequestDto(
                null, null, null, null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, -120, null
        );

        when(inventoryRepository.findById(10L)).thenReturn(Optional.of(item));

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> inventoryService.adjustStock(10L, adjustDto));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verify(inventoryRepository, never()).save(any(InventoryItem.class));
    }

    @Test
    @DisplayName("Should delete inventory item successfully")
    void testDeleteInventoryItem() {
        // Arrange
        when(inventoryRepository.findById(10L)).thenReturn(Optional.of(item));
        doNothing().when(inventoryRepository).delete(any(InventoryItem.class));

        // Act
        inventoryService.deleteInventoryItem(10L);

        // Assert
        verify(inventoryRepository, times(1)).delete(item);
    }
}

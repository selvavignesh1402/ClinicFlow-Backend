package com.HospitalManagement.service;

import com.HospitalManagement.entity.Encounter;
import com.HospitalManagement.entity.LabOrder;
import com.HospitalManagement.entity.Patient;
import com.HospitalManagement.entity.User;
import com.HospitalManagement.enums.LabOrderStatus;
import com.HospitalManagement.repository.EncounterRepository;
import com.HospitalManagement.repository.LabOrderRepository;
import com.HospitalManagement.repository.LabResultRepository;
import com.HospitalManagement.repository.PatientRepository;
import com.HospitalManagement.requestdto.LabOrderRequestDto;
import com.HospitalManagement.responsedto.LabOrderResponseDto;
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
@DisplayName("Lab Order Service Tests")
class LabOrderServiceTest {

    @Mock
    private LabOrderRepository labOrderRepository;

    @Mock
    private LabResultRepository labResultRepository;

    @Mock
    private EncounterRepository encounterRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private AuthService authService;

    @InjectMocks
    private LabOrderService labOrderService;

    private Patient patient;
    private Encounter encounter;
    private User currentUser;
    private LabOrder labOrder;
    private LabOrderRequestDto requestDto;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();

        patient = Patient.builder()
                .patientId(1L)
                .name("Jane Patient")
                .mrn("MRN-2026-999")
                .build();

        encounter = new Encounter();
        encounter.setEncounterId(2L);
        encounter.setPatient(patient);

        currentUser = User.builder()
                .userId(50L)
                .name("Lab Tech Joe")
                .build();

        labOrder = new LabOrder();
        labOrder.setLabOrderId(10L);
        labOrder.setEncounter(encounter);
        labOrder.setPatient(patient);
        labOrder.setOrderedBy(currentUser);
        labOrder.setTestsJson("{\"tests\":[\"CBC\"]}");
        labOrder.setSampleId("SMP-1234");
        labOrder.setStatus(LabOrderStatus.ORDERED);
        labOrder.setResultUri("/api/v1/lab/orders/10/results");

        requestDto = new LabOrderRequestDto(
                2L,
                "{\"tests\":[\"CBC\"]}",
                "SMP-1234",
                now
        );
    }

    @Test
    @DisplayName("Should retrieve all lab orders successfully")
    void testGetAllOrders() {
        // Arrange
        when(labOrderRepository.findAll()).thenReturn(Arrays.asList(labOrder));
        when(labResultRepository.findAllByLabOrderLabOrderId(10L)).thenReturn(Collections.emptyList());

        // Act
        List<LabOrderResponseDto> result = labOrderService.getAllOrders();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("SMP-1234", result.get(0).sampleId());
        verify(labOrderRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should retrieve lab order by ID successfully")
    void testGetOrderById() {
        // Arrange
        when(labOrderRepository.findById(10L)).thenReturn(Optional.of(labOrder));
        when(labResultRepository.findAllByLabOrderLabOrderId(10L)).thenReturn(Collections.emptyList());

        // Act
        LabOrderResponseDto result = labOrderService.getOrderById(10L);

        // Assert
        assertNotNull(result);
        assertEquals(10L, result.labOrderId());
        assertEquals(LabOrderStatus.ORDERED, result.status());
        verify(labOrderRepository, times(1)).findById(10L);
    }

    @Test
    @DisplayName("Should create lab order successfully with auto-generated result URI")
    void testCreateOrder() {
        // Arrange
        when(authService.getAuthenticatedUser()).thenReturn(currentUser);
        when(encounterRepository.findById(2L)).thenReturn(Optional.of(encounter));
        when(labOrderRepository.save(any(LabOrder.class))).thenAnswer(invocation -> {
            LabOrder lo = invocation.getArgument(0);
            lo.setLabOrderId(10L);
            return lo;
        });

        // Act
        LabOrderResponseDto result = labOrderService.createOrder(requestDto);

        // Assert
        assertNotNull(result);
        assertEquals(10L, result.labOrderId());
        assertEquals("/api/v1/lab/orders/10/results", result.resultUri());
        verify(labOrderRepository, atLeastOnce()).save(any(LabOrder.class));
    }

    @Test
    @DisplayName("Should update lab order tests before sample collection successfully")
    void testUpdateOrderSuccess() {
        // Arrange
        LabOrderRequestDto updateDto = new LabOrderRequestDto(2L, "{\"tests\":[\"CBC\",\"CRP\"]}", "SMP-1234", now);
        when(labOrderRepository.findById(10L)).thenReturn(Optional.of(labOrder));
        when(labOrderRepository.save(any(LabOrder.class))).thenReturn(labOrder);

        // Act
        LabOrderResponseDto result = labOrderService.updateOrder(10L, updateDto);

        // Assert
        assertNotNull(result);
        verify(labOrderRepository, times(1)).save(labOrder);
    }

    @Test
    @DisplayName("Should throw exception when updating lab order after sample collection")
    void testUpdateOrderAfterCollectionThrowsException() {
        // Arrange
        labOrder.setStatus(LabOrderStatus.COLLECTED);
        when(labOrderRepository.findById(10L)).thenReturn(Optional.of(labOrder));

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> labOrderService.updateOrder(10L, requestDto));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Lab order cannot be modified after sample collection", exception.getReason());
    }

    @Test
    @DisplayName("Should collect sample successfully and progress order status")
    void testCollectSample() {
        // Arrange
        when(labOrderRepository.findById(10L)).thenReturn(Optional.of(labOrder));
        when(labOrderRepository.save(any(LabOrder.class))).thenReturn(labOrder);

        // Act
        LabOrderResponseDto result = labOrderService.collectSample(10L);

        // Assert
        assertNotNull(result);
        assertEquals(LabOrderStatus.COLLECTED, result.status());
        verify(labOrderRepository, times(1)).save(labOrder);
    }

    @Test
    @DisplayName("Should cancel lab order successfully before results reported")
    void testCancelOrder() {
        // Arrange
        when(labOrderRepository.findById(10L)).thenReturn(Optional.of(labOrder));
        when(labOrderRepository.save(any(LabOrder.class))).thenReturn(labOrder);

        // Act
        LabOrderResponseDto result = labOrderService.cancelOrder(10L);

        // Assert
        assertNotNull(result);
        assertEquals(LabOrderStatus.CANCELLED, result.status());
        verify(labOrderRepository, times(1)).save(labOrder);
    }

    @Test
    @DisplayName("Should throw exception when cancelling lab order with already reported results")
    void testCancelOrderReportedThrowsException() {
        // Arrange
        labOrder.setStatus(LabOrderStatus.RESULTS_REPORTED);
        when(labOrderRepository.findById(10L)).thenReturn(Optional.of(labOrder));

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> labOrderService.cancelOrder(10L));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Cannot cancel a lab order with reported results", exception.getReason());
    }
}

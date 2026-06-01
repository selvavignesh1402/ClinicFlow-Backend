package com.HospitalManagement.service;

import com.HospitalManagement.entity.LabOrder;
import com.HospitalManagement.entity.LabResult;
import com.HospitalManagement.entity.User;
import com.HospitalManagement.enums.LabOrderStatus;
import com.HospitalManagement.enums.LabResultFlag;
import com.HospitalManagement.repository.LabOrderRepository;
import com.HospitalManagement.repository.LabResultRepository;
import com.HospitalManagement.requestdto.LabResultRequestDto;
import com.HospitalManagement.responsedto.LabResultResponseDto;
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
@DisplayName("Lab Result Service Tests")
class LabResultServiceTest {

    @Mock
    private LabResultRepository labResultRepository;

    @Mock
    private LabOrderRepository labOrderRepository;

    @Mock
    private AuthService authService;

    @InjectMocks
    private LabResultService labResultService;

    private LabOrder labOrder;
    private User currentUser;
    private LabResult labResult;
    private LabResultRequestDto requestDto;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();

        labOrder = new LabOrder();
        labOrder.setLabOrderId(10L);
        labOrder.setStatus(LabOrderStatus.COLLECTED);
        labOrder.setResultUri("/api/v1/lab/orders/10/results");
        labOrder.setCollectedAt(now);

        currentUser = User.builder()
                .userId(50L)
                .name("Lab Tech Joe")
                .build();

        labResult = new LabResult();
        labResult.setResultId(100L);
        labResult.setLabOrder(labOrder);
        labResult.setTestCode("WBC");
        labResult.setValue("8500");
        labResult.setUnits("cells/uL");
        labResult.setReferenceRangeJson("{\"min\":4000,\"max\":11000}");
        labResult.setFlag(LabResultFlag.NORMAL);
        labResult.setReportedAt(now);
        labResult.setReportedBy(currentUser);

        requestDto = new LabResultRequestDto(
                10L,
                "WBC",
                "8500",
                "cells/uL",
                "{\"min\":4000,\"max\":11000}",
                LabResultFlag.NORMAL,
                now
        );
    }

    @Test
    @DisplayName("Should retrieve all lab results successfully")
    void testGetAllResults() {
        // Arrange
        when(labResultRepository.findAll()).thenReturn(Arrays.asList(labResult));

        // Act
        List<LabResultResponseDto> result = labResultService.getAllResults();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("WBC", result.get(0).testCode());
        verify(labResultRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should retrieve lab result by ID successfully")
    void testGetResultById() {
        // Arrange
        when(labResultRepository.findById(100L)).thenReturn(Optional.of(labResult));

        // Act
        LabResultResponseDto result = labResultService.getResultById(100L);

        // Assert
        assertNotNull(result);
        assertEquals(100L, result.resultId());
        assertEquals("8500", result.value());
        verify(labResultRepository, times(1)).findById(100L);
    }

    @Test
    @DisplayName("Should throw exception when lab result not found by ID")
    void testGetResultByIdNotFound() {
        // Arrange
        when(labResultRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> labResultService.getResultById(999L));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    @DisplayName("Should create lab result successfully and update lab order status to RESULTS_REPORTED")
    void testCreateResultNormal() {
        // Arrange
        when(labOrderRepository.findById(10L)).thenReturn(Optional.of(labOrder));
        when(authService.getAuthenticatedUser()).thenReturn(currentUser);
        when(labResultRepository.save(any(LabResult.class))).thenReturn(labResult);
        // During DDL status updates, find all results for order 10
        when(labResultRepository.findAllByLabOrderLabOrderId(10L)).thenReturn(Arrays.asList(labResult));

        // Act
        LabResultResponseDto result = labResultService.createResult(requestDto);

        // Assert
        assertNotNull(result);
        assertEquals(100L, result.resultId());
        assertEquals(LabOrderStatus.RESULTS_REPORTED, labOrder.getStatus());
        verify(labResultRepository, times(1)).save(any(LabResult.class));
        verify(labOrderRepository, times(1)).save(labOrder);
    }

    @Test
    @DisplayName("Should create lab result successfully and update lab order status to CRITICAL_REPORTED")
    void testCreateResultCritical() {
        // Arrange
        LabResultRequestDto criticalDto = new LabResultRequestDto(
                10L, "WBC", "25000", "cells/uL", null, LabResultFlag.CRITICAL, now
        );

        LabResult criticalResult = new LabResult();
        criticalResult.setResultId(101L);
        criticalResult.setLabOrder(labOrder);
        criticalResult.setFlag(LabResultFlag.CRITICAL);
        criticalResult.setReportedBy(currentUser);

        when(labOrderRepository.findById(10L)).thenReturn(Optional.of(labOrder));
        when(authService.getAuthenticatedUser()).thenReturn(currentUser);
        when(labResultRepository.save(any(LabResult.class))).thenReturn(criticalResult);
        when(labResultRepository.findAllByLabOrderLabOrderId(10L)).thenReturn(Arrays.asList(criticalResult));

        // Act
        LabResultResponseDto result = labResultService.createResult(criticalDto);

        // Assert
        assertNotNull(result);
        assertEquals(LabOrderStatus.CRITICAL_REPORTED, labOrder.getStatus());
        verify(labOrderRepository, times(1)).save(labOrder);
    }

    @Test
    @DisplayName("Should delete lab result successfully and revert lab order status to COLLECTED")
    void testDeleteResult() {
        // Arrange
        when(labResultRepository.findById(100L)).thenReturn(Optional.of(labResult));
        doNothing().when(labResultRepository).delete(any(LabResult.class));
        // Order has no more results after deletion
        when(labResultRepository.findAllByLabOrderLabOrderId(10L)).thenReturn(Collections.emptyList());

        // Act
        labResultService.deleteResult(100L);

        // Assert
        assertEquals(LabOrderStatus.COLLECTED, labOrder.getStatus());
        verify(labResultRepository, times(1)).delete(labResult);
        verify(labOrderRepository, times(1)).save(labOrder);
    }
}

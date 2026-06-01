package com.HospitalManagement.service;

import com.HospitalManagement.entity.Invoice;
import com.HospitalManagement.entity.Patient;
import com.HospitalManagement.entity.Payment;
import com.HospitalManagement.enums.InvoiceStatus;
import com.HospitalManagement.exception.BadRequestException;
import com.HospitalManagement.exception.ResourceNotFoundException;
import com.HospitalManagement.repository.PatientRepository;
import com.HospitalManagement.repository.PaymentRepository;
import com.HospitalManagement.requestdto.PaymentRequestDto;
import com.HospitalManagement.responsedto.PaymentResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Payment Service Tests")
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private InvoiceService invoiceService;

    @InjectMocks
    private PaymentService paymentService;

    private Patient patient;
    private Invoice invoice;
    private Payment payment;
    private PaymentRequestDto requestDto;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();

        patient = Patient.builder()
                .patientId(1L)
                .name("Priya Nair")
                .mrn("MRN-2026-001")
                .build();

        invoice = new Invoice();
        invoice.setInvoiceId(10L);
        invoice.setPatient(patient);
        invoice.setTotalAmount(500.0);
        invoice.setStatus(InvoiceStatus.UNPAID);

        payment = new Payment();
        payment.setPaymentId(50L);
        payment.setInvoice(invoice);
        payment.setPatient(patient);
        payment.setAmount(200.0);
        payment.setMethod("CARD");
        payment.setPaidAt(now);
        payment.setStatus("COMPLETED");

        requestDto = new PaymentRequestDto(
                10L,
                1L,
                200.0,
                "CARD",
                now,
                "COMPLETED"
        );
    }

    @Test
    @DisplayName("Should retrieve all payments successfully")
    void testGetAllPayments() {
        // Arrange
        when(paymentRepository.findAll()).thenReturn(Arrays.asList(payment));

        // Act
        List<PaymentResponseDto> result = paymentService.getAllPayments();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(200.0, result.get(0).amount());
        verify(paymentRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should retrieve payment by ID successfully")
    void testGetPaymentById() {
        // Arrange
        when(paymentRepository.findById(50L)).thenReturn(Optional.of(payment));

        // Act
        PaymentResponseDto result = paymentService.getPaymentById(50L);

        // Assert
        assertNotNull(result);
        assertEquals(50L, result.paymentId());
        verify(paymentRepository, times(1)).findById(50L);
    }

    @Test
    @DisplayName("Should throw exception when payment not found by ID")
    void testGetPaymentNotFound() {
        // Arrange
        when(paymentRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> paymentService.getPaymentById(999L));
        assertEquals("Payment not found with id: 999", exception.getMessage());
    }

    @Test
    @DisplayName("Should record manual payment successfully and mark invoice partially paid")
    void testRecordManualPaymentPartiallyPaid() {
        // Arrange
        when(invoiceService.getInvoiceEntity(10L)).thenReturn(invoice);
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        when(paymentRepository.findByInvoiceInvoiceId(10L)).thenReturn(Arrays.asList(payment));

        // Act
        PaymentResponseDto result = paymentService.recordManualPayment(requestDto);

        // Assert
        assertNotNull(result);
        assertEquals("COMPLETED", result.status());
        verify(paymentRepository, times(1)).save(any(Payment.class));
        verify(invoiceService, times(1)).updateInvoiceStatus(10L, "PARTIALLY_PAID");
    }

    @Test
    @DisplayName("Should record manual payment successfully and mark invoice fully paid")
    void testRecordManualPaymentFullyPaid() {
        // Arrange
        PaymentRequestDto fullPaymentRequest = new PaymentRequestDto(
                10L, 1L, 500.0, "CARD", now, "COMPLETED"
        );

        Payment fullPayment = new Payment();
        fullPayment.setPaymentId(50L);
        fullPayment.setInvoice(invoice);
        fullPayment.setPatient(patient);
        fullPayment.setAmount(500.0);

        when(invoiceService.getInvoiceEntity(10L)).thenReturn(invoice);
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(paymentRepository.save(any(Payment.class))).thenReturn(fullPayment);
        when(paymentRepository.findByInvoiceInvoiceId(10L)).thenReturn(Arrays.asList(fullPayment));

        // Act
        PaymentResponseDto result = paymentService.recordManualPayment(fullPaymentRequest);

        // Assert
        assertNotNull(result);
        verify(invoiceService, times(1)).updateInvoiceStatus(10L, "PAID");
    }

    @Test
    @DisplayName("Should throw exception when payment patient does not match invoice patient")
    void testRecordManualPaymentPatientMismatch() {
        // Arrange
        Patient alternatePatient = Patient.builder().patientId(2L).name("Arjun Verma").build();
        when(invoiceService.getInvoiceEntity(10L)).thenReturn(invoice);
        when(patientRepository.findById(1L)).thenReturn(Optional.of(alternatePatient)); // returns mismatched patient

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> paymentService.recordManualPayment(requestDto));
        assertEquals("Payment patient does not match invoice patient", exception.getMessage());
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    @DisplayName("Should throw exception when invoice is already paid")
    void testRecordManualPaymentAlreadyPaid() {
        // Arrange
        invoice.setStatus(InvoiceStatus.PAID);
        when(invoiceService.getInvoiceEntity(10L)).thenReturn(invoice);
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> paymentService.recordManualPayment(requestDto));
        assertEquals("Invoice is already paid", exception.getMessage());
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    @DisplayName("Should throw exception when payment amount is negative")
    void testRecordManualPaymentInvalidAmount() {
        // Arrange
        PaymentRequestDto invalidRequest = new PaymentRequestDto(
                10L, 1L, -50.0, "CARD", now, "COMPLETED"
        );
        when(invoiceService.getInvoiceEntity(10L)).thenReturn(invoice);
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> paymentService.recordManualPayment(invalidRequest));
        assertEquals("Payment amount must be greater than zero", exception.getMessage());
        verify(paymentRepository, never()).save(any(Payment.class));
    }
}

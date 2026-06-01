package com.HospitalManagement.service;

import com.HospitalManagement.entity.Encounter;
import com.HospitalManagement.entity.Invoice;
import com.HospitalManagement.entity.Patient;
import com.HospitalManagement.enums.InvoiceStatus;
import com.HospitalManagement.exception.ResourceNotFoundException;
import com.HospitalManagement.repository.EncounterRepository;
import com.HospitalManagement.repository.InvoiceRepository;
import com.HospitalManagement.repository.PatientRepository;
import com.HospitalManagement.requestdto.InvoiceRequestDto;
import com.HospitalManagement.responsedto.InvoiceResponseDto;
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
@DisplayName("Invoice Service Tests")
class InvoiceServiceTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private EncounterRepository encounterRepository;

    @InjectMocks
    private InvoiceService invoiceService;

    private Patient patient;
    private Encounter encounter;
    private Invoice invoice;
    private InvoiceRequestDto requestDto;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();

        patient = Patient.builder()
                .patientId(1L)
                .name("Priya Nair")
                .mrn("MRN-2026-001")
                .build();

        encounter = new Encounter();
        encounter.setEncounterId(2L);
        encounter.setPatient(patient);

        invoice = new Invoice();
        invoice.setInvoiceId(10L);
        invoice.setPatient(patient);
        invoice.setEncounter(encounter);
        invoice.setLineItemsJson("[]");
        invoice.setSubtotal(100.0);
        invoice.setTaxes(10.0);
        invoice.setDiscounts(5.0);
        invoice.setTotalAmount(105.0);
        invoice.setStatus(InvoiceStatus.UNPAID);
        invoice.setIssuedAt(now);
        invoice.setDueDate(now.plusDays(1));

        requestDto = new InvoiceRequestDto(
                1L,
                2L,
                "[]",
                100.0,
                10.0,
                5.0,
                105.0,
                now,
                now.plusDays(1),
                "UNPAID"
        );
    }

    @Test
    @DisplayName("Should retrieve all invoices successfully")
    void testGetAllInvoices() {
        // Arrange
        when(invoiceRepository.findAll()).thenReturn(Arrays.asList(invoice));

        // Act
        List<InvoiceResponseDto> result = invoiceService.getAllInvoices();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(105.0, result.get(0).totalAmount());
        verify(invoiceRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should retrieve invoice by ID successfully")
    void testGetInvoiceById() {
        // Arrange
        when(invoiceRepository.findById(10L)).thenReturn(Optional.of(invoice));

        // Act
        InvoiceResponseDto result = invoiceService.getInvoice(10L);

        // Assert
        assertNotNull(result);
        assertEquals(10L, result.invoiceId());
        assertEquals(105.0, result.totalAmount());
        verify(invoiceRepository, times(1)).findById(10L);
    }

    @Test
    @DisplayName("Should throw exception when invoice not found by ID")
    void testGetInvoiceNotFound() {
        // Arrange
        when(invoiceRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> invoiceService.getInvoice(999L));
        assertEquals("Invoice not found with id: 999", exception.getMessage());
    }

    @Test
    @DisplayName("Should retrieve invoices for patient ID successfully")
    void testGetPatientInvoices() {
        // Arrange
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(invoiceRepository.findByPatientPatientId(1L)).thenReturn(Arrays.asList(invoice));

        // Act
        List<InvoiceResponseDto> result = invoiceService.getPatientInvoices(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(invoiceRepository, times(1)).findByPatientPatientId(1L);
    }

    @Test
    @DisplayName("Should create invoice successfully and auto-calculate total amount if null")
    void testCreateInvoiceAutoCalculateTotal() {
        // Arrange
        InvoiceRequestDto requestWithNullTotal = new InvoiceRequestDto(
                1L, 2L, "[]", 100.0, 10.0, 5.0, null, now, now.plusDays(1), "UNPAID"
        );

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(encounterRepository.findById(2L)).thenReturn(Optional.of(encounter));
        when(invoiceRepository.save(any(Invoice.class))).thenAnswer(invocation -> {
            Invoice inv = invocation.getArgument(0);
            inv.setInvoiceId(10L);
            return inv;
        });

        // Act
        InvoiceResponseDto result = invoiceService.createInvoice(requestWithNullTotal);

        // Assert
        assertNotNull(result);
        assertEquals(105.0, result.totalAmount()); // 100 + 10 - 5
        verify(invoiceRepository, times(1)).save(any(Invoice.class));
    }

    @Test
    @DisplayName("Should update invoice status successfully")
    void testUpdateInvoiceStatus() {
        // Arrange
        when(invoiceRepository.findById(10L)).thenReturn(Optional.of(invoice));
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(invoice);

        // Act
        InvoiceResponseDto result = invoiceService.updateInvoiceStatus(10L, "PAID");

        // Assert
        assertNotNull(result);
        assertEquals(InvoiceStatus.PAID, result.status());
        verify(invoiceRepository, times(1)).save(invoice);
    }

    @Test
    @DisplayName("Should delete invoice successfully")
    void testDeleteInvoice() {
        // Arrange
        when(invoiceRepository.findById(10L)).thenReturn(Optional.of(invoice));
        doNothing().when(invoiceRepository).delete(any(Invoice.class));

        // Act
        invoiceService.deleteInvoice(10L);

        // Assert
        verify(invoiceRepository, times(1)).delete(invoice);
    }
}

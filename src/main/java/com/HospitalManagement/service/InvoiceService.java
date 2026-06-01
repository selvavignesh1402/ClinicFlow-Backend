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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class InvoiceService {

    private static final Logger logger = LoggerFactory.getLogger(InvoiceService.class);

    private final InvoiceRepository invoiceRepository;
    private final PatientRepository patientRepository;
    private final EncounterRepository encounterRepository;

    public InvoiceResponseDto createInvoice(InvoiceRequestDto requestDto) {
        logger.info("Creating invoice - Patient ID: {}, Amount: {}", requestDto.patientId(), requestDto.totalAmount());
        Patient patient = findPatient(requestDto.patientId());
        Encounter encounter = requestDto.encounterId() == null ? null : findEncounter(requestDto.encounterId());
        Invoice invoice = new Invoice();
        fillInvoice(invoice, requestDto, patient, encounter);
        invoice.setIssuedAt(LocalDateTime.now());
        invoice.setDueDate(requestDto.dueDate() == null ? LocalDateTime.now().plusDays(1) : requestDto.dueDate());
        invoice.setStatus(InvoiceStatus.UNPAID);
        Invoice savedInvoice = invoiceRepository.save(invoice);
        logger.info("Successfully created invoice - ID: {}, Amount: {}", savedInvoice.getInvoiceId(), requestDto.totalAmount());
        return toResponse(savedInvoice);
    }

    public InvoiceResponseDto updateInvoice(Long invoiceId, InvoiceRequestDto requestDto) {
        logger.info("Updating invoice - ID: {}", invoiceId);
        Invoice invoice = findInvoice(invoiceId);
        Patient patient = findPatient(requestDto.patientId());
        Encounter encounter = requestDto.encounterId() == null ? null : findEncounter(requestDto.encounterId());
        fillInvoice(invoice, requestDto, patient, encounter);
        if (requestDto.issuedAt() != null) {
            invoice.setIssuedAt(requestDto.issuedAt());
        }
        if (requestDto.dueDate() != null) {
            invoice.setDueDate(requestDto.dueDate());
        }
        if (requestDto.status() != null && !requestDto.status().isBlank()) {
            invoice.setStatus(InvoiceStatus.valueOf(requestDto.status().toUpperCase()));
        }

        Invoice updatedInvoice = invoiceRepository.save(invoice);
        logger.info("Successfully updated invoice - ID: {}", invoiceId);
        return toResponse(updatedInvoice);
    }

    public void deleteInvoice(Long invoiceId) {
        logger.info("Deleting invoice - ID: {}", invoiceId);
        invoiceRepository.delete(findInvoice(invoiceId));
        logger.info("Successfully deleted invoice - ID: {}", invoiceId);
    }

    @Transactional(readOnly = true)
    public InvoiceResponseDto getInvoice(Long invoiceId) {
        logger.debug("Fetching invoice by ID: {}", invoiceId);
        InvoiceResponseDto invoice = toResponse(findInvoice(invoiceId));
        logger.info("Retrieved invoice - ID: {}, Amount: {}", invoiceId, invoice.totalAmount());
        return invoice;
    }

    @Transactional(readOnly = true)
    public Invoice getInvoiceEntity(Long invoiceId) {
        logger.debug("Fetching invoice entity by ID: {}", invoiceId);
        Invoice invoice = findInvoice(invoiceId);
        logger.debug("Retrieved invoice entity - ID: {}", invoiceId);
        return invoice;
    }

    @Transactional(readOnly = true)
    public List<InvoiceResponseDto> getAllInvoices() {
        logger.debug("Fetching all invoices");
        List<Invoice> invoices = invoiceRepository.findAll();
        List<InvoiceResponseDto> response = new ArrayList<>();
        for (Invoice invoice : invoices) {
            response.add(toResponse(invoice));
        }
        logger.info("Retrieved {} invoices", response.size());
        return response;
    }

    @Transactional(readOnly = true)
    public List<InvoiceResponseDto> getPatientInvoices(Long patientId) {
        logger.debug("Fetching invoices for patient ID: {}", patientId);
        findPatient(patientId);
        List<Invoice> invoices = invoiceRepository.findByPatientPatientId(patientId);
        List<InvoiceResponseDto> response = new ArrayList<>();
        for (Invoice invoice : invoices) {
            response.add(toResponse(invoice));
        }
        logger.info("Retrieved {} invoices for patient ID: {}", response.size(), patientId);
        return response;
    }

    public InvoiceResponseDto updateInvoiceStatus(Long invoiceId, String status) {
        logger.info("Updating invoice status - ID: {}, Status: {}", invoiceId, status);
        Invoice invoice = findInvoice(invoiceId);
        if (status != null) {
            invoice.setStatus(InvoiceStatus.valueOf(status.toUpperCase()));
        }
        Invoice updatedInvoice = invoiceRepository.save(invoice);
        logger.info("Successfully updated invoice status - ID: {}, Status: {}", invoiceId, status);
        return toResponse(updatedInvoice);
    }

    private void fillInvoice(Invoice invoice, InvoiceRequestDto requestDto, Patient patient, Encounter encounter) {
        invoice.setPatient(patient);
        invoice.setEncounter(encounter);
        invoice.setLineItemsJson(requestDto.lineItemsJson());
        invoice.setSubtotal(requestDto.subtotal());
        invoice.setTaxes(requestDto.taxes());
        invoice.setDiscounts(requestDto.discounts());
        if (requestDto.totalAmount() == null) {
            invoice.setTotalAmount(requestDto.subtotal() + requestDto.taxes() - requestDto.discounts());
        } else {
            invoice.setTotalAmount(requestDto.totalAmount());
        }
    }

    private Invoice findInvoice(Long invoiceId) {
        logger.debug("Looking up invoice by ID: {}", invoiceId);
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + invoiceId));
        logger.debug("Found invoice - ID: {}, Amount: {}", invoiceId, invoice.getTotalAmount());
        return invoice;
    }

    private Patient findPatient(Long patientId) {
        logger.debug("Looking up patient by ID: {}", patientId);
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + patientId));
        logger.debug("Found patient - ID: {}, Name: {}", patientId, patient.getName());
        return patient;
    }

    private Encounter findEncounter(Long encounterId) {
        logger.debug("Looking up encounter by ID: {}", encounterId);
        Encounter encounter = encounterRepository.findById(encounterId)
                .orElseThrow(() -> new ResourceNotFoundException("Encounter not found with id: " + encounterId));
        logger.debug("Found encounter - ID: {}", encounterId);
        return encounter;
    }

    private InvoiceResponseDto toResponse(Invoice invoice) {
        String patientName = null;
        if (invoice.getPatient() != null) {
            patientName = invoice.getPatient().getName();
        }
        Long encounterId = null;
        if (invoice.getEncounter() != null) {
            encounterId = invoice.getEncounter().getEncounterId();
        }
        return new InvoiceResponseDto(
                invoice.getInvoiceId(),
                invoice.getPatient().getPatientId(),
                patientName,
                encounterId,
                invoice.getLineItemsJson(),
                invoice.getSubtotal(),
                invoice.getTaxes(),
                invoice.getDiscounts(),
                invoice.getTotalAmount(),
                invoice.getIssuedAt(),
                invoice.getDueDate(),
                invoice.getStatus()
        );
    }

}
package com.HospitalManagement.controller;

import com.HospitalManagement.requestdto.InvoiceRequestDto;
import com.HospitalManagement.responsedto.InvoiceResponseDto;
import com.HospitalManagement.service.InvoiceService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/finance/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('FINANCE_OFFICER', 'ADMIN', 'RECEPTION')")
    public InvoiceResponseDto createInvoice(@Valid @RequestBody InvoiceRequestDto requestDto) {
        return invoiceService.createInvoice(requestDto);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('FINANCE_OFFICER', 'ADMIN', 'CLINIC_MANAGER', 'RECEPTION')")
    public List<InvoiceResponseDto> getAllInvoices() {
        return invoiceService.getAllInvoices();
    }

    @GetMapping("/{invoiceId}")
    @PreAuthorize("hasAnyAuthority('FINANCE_OFFICER', 'ADMIN', 'CLINIC_MANAGER', 'RECEPTION')")
    public InvoiceResponseDto getInvoice(@PathVariable Long invoiceId) {
        return invoiceService.getInvoice(invoiceId);
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyAuthority('FINANCE_OFFICER', 'ADMIN', 'CLINIC_MANAGER', 'RECEPTION')")
    public List<InvoiceResponseDto> getPatientInvoices(@PathVariable Long patientId) {
        return invoiceService.getPatientInvoices(patientId);
    }

    @PutMapping("/{invoiceId}")
    @PreAuthorize("hasAnyAuthority('FINANCE_OFFICER', 'ADMIN')")
    public InvoiceResponseDto updateInvoice(@PathVariable Long invoiceId, @Valid @RequestBody InvoiceRequestDto requestDto) {
        return invoiceService.updateInvoice(invoiceId, requestDto);
    }

    @PatchMapping("/{invoiceId}/status/{status}")
    @PreAuthorize("hasAnyAuthority('FINANCE_OFFICER', 'ADMIN')")
    public InvoiceResponseDto updateInvoiceStatus(@PathVariable Long invoiceId, @PathVariable String status) {
        return invoiceService.updateInvoiceStatus(invoiceId, status);
    }

    @DeleteMapping("/{invoiceId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('ADMIN')")
    public void deleteInvoice(@PathVariable Long invoiceId) {
        invoiceService.deleteInvoice(invoiceId);
    }
}
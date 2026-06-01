package com.HospitalManagement.controller;

import com.HospitalManagement.requestdto.LabOrderRequestDto;
import com.HospitalManagement.requestdto.LabResultRequestDto;
import com.HospitalManagement.responsedto.LabOrderResponseDto;
import com.HospitalManagement.responsedto.LabResultResponseDto;
import com.HospitalManagement.service.LabOrderService;
import com.HospitalManagement.service.LabResultService;
import jakarta.validation.Valid;
import java.util.List;
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
@RequestMapping("/api/v1/lab")
public class LabController {

    private final LabOrderService labOrderService;
    private final LabResultService labResultService;

    public LabController(LabOrderService labOrderService, LabResultService labResultService) {
        this.labOrderService = labOrderService;
        this.labResultService = labResultService;
    }

    @GetMapping("/orders")
    @PreAuthorize("hasAnyAuthority('LAB_TECHNICIAN', 'CLINICIAN', 'ADMIN')")
    public List<LabOrderResponseDto> getAllOrders() {
        return labOrderService.getAllOrders();
    }

    @GetMapping("/orders/{labOrderId}")
    @PreAuthorize("hasAnyAuthority('LAB_TECHNICIAN', 'CLINICIAN', 'ADMIN')")
    public LabOrderResponseDto getOrderById(@PathVariable Long labOrderId) {
        return labOrderService.getOrderById(labOrderId);
    }

    @GetMapping("/orders/patient/{patientId}")
    @PreAuthorize("hasAnyAuthority('LAB_TECHNICIAN', 'CLINICIAN', 'ADMIN')")
    public List<LabOrderResponseDto> getOrdersByPatientId(@PathVariable Long patientId) {
        return labOrderService.getOrdersByPatientId(patientId);
    }

    @PostMapping("/orders")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('CLINICIAN')")
    public LabOrderResponseDto createOrder(@Valid @RequestBody LabOrderRequestDto requestDto) {
        return labOrderService.createOrder(requestDto);
    }

    @PutMapping("/orders/{labOrderId}")
    @PreAuthorize("hasAuthority('CLINICIAN')")
    public LabOrderResponseDto updateOrder(
            @PathVariable Long labOrderId,
            @Valid @RequestBody LabOrderRequestDto requestDto
    ) {
        return labOrderService.updateOrder(labOrderId, requestDto);
    }

    @DeleteMapping("/orders/{labOrderId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('LAB_TECHNICIAN', 'ADMIN')")
    @PatchMapping("/lab-orders/{labOrderId}/cancel")
    public LabOrderResponseDto cancelOrder(@PathVariable Long labOrderId) {
        return labOrderService.cancelOrder(labOrderId);
    }

    @PatchMapping("/orders/{labOrderId}/collect")
    @PreAuthorize("hasAuthority('LAB_TECHNICIAN')")
    public LabOrderResponseDto collectSample(@PathVariable Long labOrderId) {
        return labOrderService.collectSample(labOrderId);
    }

    @GetMapping("/results")
    @PreAuthorize("hasAuthority('LAB_TECHNICIAN')")
    public List<LabResultResponseDto> getAllResults() {
        return labResultService.getAllResults();
    }

    @GetMapping("/results/{resultId}")
    @PreAuthorize("hasAnyAuthority('LAB_TECHNICIAN', 'CLINICIAN', 'ADMIN')")
    public LabResultResponseDto getResultById(@PathVariable Long resultId) {
        return labResultService.getResultById(resultId);
    }

    @GetMapping("/orders/{labOrderId}/results")
    @PreAuthorize("hasAnyAuthority('LAB_TECHNICIAN', 'CLINICIAN', 'ADMIN')")
    public List<LabResultResponseDto> getResultsByOrderId(@PathVariable Long labOrderId) {
        return labResultService.getResultsByOrderId(labOrderId);
    }

    @PostMapping("/results")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('LAB_TECHNICIAN')")
    public LabResultResponseDto createResult(@Valid @RequestBody LabResultRequestDto requestDto) {
        return labResultService.createResult(requestDto);
    }

    @PutMapping("/results/{resultId}")
    @PreAuthorize("hasAuthority('LAB_TECHNICIAN')")
    public LabResultResponseDto updateResult(
            @PathVariable Long resultId,
            @Valid @RequestBody LabResultRequestDto requestDto
    ) {
        return labResultService.updateResult(resultId, requestDto);
    }

    @DeleteMapping("/results/{resultId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('LAB_TECHNICIAN', 'ADMIN')")
    public void deleteResult(@PathVariable Long resultId) {
        labResultService.deleteResult(resultId);
    }
}
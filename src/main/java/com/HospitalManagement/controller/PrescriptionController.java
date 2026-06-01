package com.HospitalManagement.controller;

import com.HospitalManagement.requestdto.PrescriptionRequestDto;
import com.HospitalManagement.responsedto.PrescriptionResponseDto;
import com.HospitalManagement.service.PrescriptionService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/prescriptions")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    public PrescriptionController(PrescriptionService prescriptionService) {
        this.prescriptionService = prescriptionService;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('CLINICIAN')")
    public List<PrescriptionResponseDto> getAllPrescriptions() {
        return prescriptionService.getAllPrescriptions();
    }

    @GetMapping("/{rxId}")
    @PreAuthorize("hasAnyAuthority('CLINICIAN','PATIENT')")
    public PrescriptionResponseDto getPrescriptionById(@PathVariable Long rxId) {
        return prescriptionService.getPrescriptionById(rxId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('CLINICIAN')")
    public PrescriptionResponseDto createPrescription(@Valid @RequestBody PrescriptionRequestDto requestDto) {
        return prescriptionService.createPrescription(requestDto);
    }

    @PutMapping("/{rxId}")
    @PreAuthorize("hasAuthority('CLINICIAN')")
    public PrescriptionResponseDto updatePrescription(
            @PathVariable("rxId") Long rxId,
            @Valid @RequestBody PrescriptionRequestDto requestDto
    ) {

        System.out.println("PATCH rxId = " + rxId);
        System.out.println("PATCH requestDto = " + requestDto);
        return prescriptionService.updatePrescription(rxId, requestDto);
    }

    @PatchMapping("/status/{rxId}")
    @PreAuthorize("hasAuthority('CLINICIAN')")
    public PrescriptionResponseDto updatePrescriptionStatus(
            @PathVariable Long rxId,
            @RequestBody PrescriptionRequestDto requestDto
    ) {
        return prescriptionService.updatePrescription(rxId, requestDto);
    }

    @DeleteMapping("/{rxId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePrescription(@PathVariable Long rxId) {
        prescriptionService.deletePrescription(rxId);
    }
}
package com.HospitalManagement.controller;

import com.HospitalManagement.requestdto.PharmacyRequestDto;
import com.HospitalManagement.responsedto.MedicationResponseDto;
import com.HospitalManagement.service.MedicationMasterService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/medications")
public class MedicationMasterController {

    private final MedicationMasterService medicationService;

    public MedicationMasterController(MedicationMasterService medicationService) {
        this.medicationService = medicationService;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('CLINICIAN', 'PHARMACY', 'ADMIN', 'CLINIC_MANAGER')")
    public List<MedicationResponseDto> getAllMedications(@RequestParam(required = false) String search) {
        return medicationService.getAllMedications(search);
    }

    @GetMapping("/{medId}")
    @PreAuthorize("hasAnyAuthority('CLINICIAN', 'PHARMACY', 'ADMIN', 'CLINIC_MANAGER')")
    public MedicationResponseDto getMedicationById(@PathVariable Long medId) {
        return medicationService.getMedicationById(medId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('PHARMACY', 'ADMIN')")
    public MedicationResponseDto createMedication(@RequestBody PharmacyRequestDto request) {
        return medicationService.createMedication(request);
    }

    @PutMapping("/{medId}")
    @PreAuthorize("hasAnyAuthority('PHARMACY', 'ADMIN')")
    public MedicationResponseDto updateMedication(@PathVariable Long medId, @RequestBody PharmacyRequestDto request) {
        return medicationService.updateMedication(medId, request);
    }

    @DeleteMapping("/{medId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('ADMIN')")
    public void deleteMedication(@PathVariable Long medId) {
        medicationService.deleteMedication(medId);
    }
}
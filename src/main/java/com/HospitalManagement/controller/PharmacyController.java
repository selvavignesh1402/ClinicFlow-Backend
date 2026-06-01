package com.HospitalManagement.controller;

import com.HospitalManagement.requestdto.PharmacyRequestDto;
import com.HospitalManagement.responsedto.DispenseResponseDto;
import com.HospitalManagement.service.PharmacyService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pharmacist")
@PreAuthorize("hasAnyAuthority('PHARMACY', 'ADMIN', 'CLINIC_MANAGER')")
public class PharmacyController {

    private final PharmacyService pharmacyService;

    public PharmacyController(PharmacyService pharmacyService) {
        this.pharmacyService = pharmacyService;
    }

    @GetMapping("/dispense-records")
    public List<DispenseResponseDto> getAllDispenseRecords() {
        return pharmacyService.getAllDispenseRecords();
    }

    @GetMapping("/dispense-records/{dispenseId}")
    public DispenseResponseDto getDispenseRecordById(@PathVariable Long dispenseId) {
        return pharmacyService.getDispenseRecordById(dispenseId);
    }

    @GetMapping("/prescriptions/{prescriptionId}/dispense-records")
    public List<DispenseResponseDto> getDispenseRecordsByPrescription(@PathVariable Long prescriptionId) {
        return pharmacyService.getDispenseRecordsByPrescription(prescriptionId);
    }

    @PostMapping("/dispense")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('PHARMACY', 'ADMIN')")
    public DispenseResponseDto dispensePrescription(@RequestBody PharmacyRequestDto request) {
        return pharmacyService.dispensePrescription(request);
    }

    @PostMapping("/dispense-records/{dispenseId}/return")
    @PreAuthorize("hasAnyAuthority('PHARMACY', 'ADMIN')")
    public DispenseResponseDto returnDispense(@PathVariable Long dispenseId, @RequestBody PharmacyRequestDto request) {
        return pharmacyService.returnDispense(dispenseId, request);
    }
}
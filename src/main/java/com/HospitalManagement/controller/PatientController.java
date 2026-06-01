package com.HospitalManagement.controller;

import com.HospitalManagement.requestdto.PatientRequestDto;
import com.HospitalManagement.responsedto.PatientResponseDto;
import com.HospitalManagement.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('RECEPTION', 'CLINICIAN', 'ADMIN', 'CLINIC_MANAGER')")
    public List<PatientResponseDto> getAllPatients() {
        return patientService.getAllPatients();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('RECEPTION', 'CLINICIAN', 'ADMIN', 'CLINIC_MANAGER', 'PATIENT')")
    public PatientResponseDto getPatientById(@PathVariable Long id) {
        return patientService.getPatientById(id);
    }

    @GetMapping("/mrn/{mrn}")
    @PreAuthorize("hasAnyAuthority('RECEPTION', 'CLINICIAN', 'ADMIN', 'CLINIC_MANAGER')")
    public PatientResponseDto getPatientByMrn(@PathVariable String mrn) {
        return patientService.getPatientByMrn(mrn);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('RECEPTION', 'ADMIN')")
    public PatientResponseDto registerPatient(@Valid @RequestBody PatientRequestDto request) {
        return patientService.registerPatient(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('RECEPTION', 'ADMIN')")
    public PatientResponseDto updatePatient(@PathVariable Long id, @Valid @RequestBody PatientRequestDto request) {
        return patientService.updatePatient(id, request);
    }

    @PatchMapping("/{id}/deactivate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('RECEPTION', 'ADMIN')")
    public void deactivatePatient(@PathVariable Long id) {
        patientService.deactivatePatient(id);
    }
}
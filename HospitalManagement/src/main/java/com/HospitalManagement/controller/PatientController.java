package com.HospitalManagement.controller;

import com.HospitalManagement.entity.Patient;
import com.HospitalManagement.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/patient")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('RECEPTION', 'CLINICIAN', 'ADMIN', 'CLINIC_MANAGER')")
    public ResponseEntity<List<Patient>> getAllPatients() {
        return ResponseEntity.ok(patientService.getAllPatients());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('RECEPTION', 'CLINICIAN', 'ADMIN', 'CLINIC_MANAGER')")
    public ResponseEntity<Patient> getPatientById(@PathVariable Long id) {
        return ResponseEntity.ok(patientService.getPatientById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('RECEPTION', 'ADMIN')")
    public ResponseEntity<Patient> registerPatient(@RequestBody Patient patient) {
        return ResponseEntity.ok(patientService.registerPatient(patient));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('RECEPTION', 'ADMIN')")
    public ResponseEntity<Patient> updatePatient(@PathVariable Long id, @RequestBody Patient patient) {
        return ResponseEntity.ok(patientService.updatePatient(id, patient));
    }
}

package com.HospitalManagement.service;

import com.HospitalManagement.entity.Patient;
import com.HospitalManagement.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;

    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    public Patient getPatientById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found with ID: " + id));
    }

    @Transactional
    public Patient registerPatient(Patient patient) {
        if (patient.getMrn() == null || patient.getMrn().isEmpty()) {
            patient.setMrn(generateMrn());
        }
        if (patientRepository.existsByMrn(patient.getMrn())) {
            throw new RuntimeException("MRN already exists!");
        }

        populatePatientDefaults(patient);
        patient.setCreatedAt(LocalDateTime.now());
        patient.setStatus("ACTIVE");
        return patientRepository.save(patient);
    }

    @Transactional
    public Patient updatePatient(Long id, Patient updatedData) {
        Patient existing = getPatientById(id);
        existing.setName(updatedData.getName());
        existing.setDob(updatedData.getDob());
        existing.setGender(updatedData.getGender());
        existing.setContactInfoJson(updatedData.getContactInfoJson());
        existing.setAddressJson(updatedData.getAddressJson());
        existing.setPrimaryContact(updatedData.getPrimaryContact());
        existing.setInsuranceId(updatedData.getInsuranceId());

        return patientRepository.save(existing);
    }

    private void populatePatientDefaults(Patient patient) {
        if (patient.getContactInfoJson() == null || patient.getContactInfoJson().isBlank()) {
            patient.setContactInfoJson("{}");
        }
        if (patient.getAddressJson() == null || patient.getAddressJson().isBlank()) {
            patient.setAddressJson("{}");
        }
    }

    private String generateMrn() {
        String mrn;
        do {
            mrn = "MRN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (patientRepository.existsByMrn(mrn));
        return mrn;
    }
}

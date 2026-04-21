package com.HospitalManagement.service;

import com.HospitalManagement.entity.Patient;
import com.HospitalManagement.entity.User;
import com.HospitalManagement.repository.PatientRepository;
import com.HospitalManagement.responsedto.PatientResponseDto;

import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PatientService {

    private final PatientRepository patientRepository;

    public List<PatientResponseDto> getAllPatients() {
        return patientRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    public PatientResponseDto getPatientById(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Patient not found with ID: " + id));

        return mapToDto(patient);
    }

    @Transactional
    public PatientResponseDto registerPatient(Patient patient) {

        if (patient.getMrn() == null || patient.getMrn().isBlank()) {
            patient.setMrn(generateMrn());
        }

        if (patientRepository.existsByMrn(patient.getMrn())) {
            throw new RuntimeException("MRN already exists!");
        }

        patient.setStatus("ACTIVE");
        patient.setCreatedAt(LocalDateTime.now());

        Patient saved = patientRepository.save(patient);
        return mapToDto(saved);
    }

    @Transactional
    public PatientResponseDto updatePatient(Long id, Patient updatedData) {

        Patient existing = patientRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Patient not found with ID: " + id));

        existing.setDob(updatedData.getDob());
        existing.setGender(updatedData.getGender());
        existing.setContactInfoJson(updatedData.getContactInfoJson());
        existing.setAddressJson(updatedData.getAddressJson());
        existing.setInsuranceId(updatedData.getInsuranceId());

        Patient saved = patientRepository.save(existing);
        return mapToDto(saved);
    }

    @Transactional
    public void deletePatient(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Patient not found with ID: " + id));

        patientRepository.delete(patient);
    }

    private PatientResponseDto mapToDto(Patient patient) {

        User user = patient.getUser();

        return new PatientResponseDto(
                patient.getPatientId(),
                patient.getMrn(),

                user != null ? user.getName() : null,
                // user != null ? user.getPhone() : null,
                patient.getPrimaryContact(),
                patient.getDob(),
                patient.getGender(),
                patient.getAddressJson(),
                patient.getInsuranceId(),
                patient.getStatus(),
                patient.getCreatedAt()
        );
    }

    private String generateMrn() {
        String mrn;

        do {
            mrn = "MRN-" + UUID.randomUUID()
                    .toString()
                    .substring(0, 8)
                    .toUpperCase();

        } while (patientRepository.existsByMrn(mrn));

        return mrn;
    }
}
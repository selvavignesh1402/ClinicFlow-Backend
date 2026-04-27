package com.HospitalManagement.service;

import com.HospitalManagement.entity.Patient;
import com.HospitalManagement.entity.User;
import com.HospitalManagement.repository.PatientRepository;
import com.HospitalManagement.repository.UserRepository;
import com.HospitalManagement.requestdto.PatientRequestDto;
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
    private final UserRepository userRepository;

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
    public PatientResponseDto registerPatient(PatientRequestDto request) {

        Patient patient = new Patient();

        if (request.mrn() == null || request.mrn().isBlank()) {
            patient.setMrn(generateMrn());
        } else {
            if (patientRepository.existsByMrn(request.mrn())) {
                throw new RuntimeException("MRN already exists!");
            }
            patient.setMrn(request.mrn());
        }

        patient.setDob(request.dob());
        patient.setGender(request.gender());
        patient.setContactInfoJson(request.contactInfoJson());
        patient.setAddressJson(request.addressJson());
        patient.setPrimaryContact(request.primaryContact());
        patient.setInsuranceId(request.insuranceId());
        patient.setStatus("ACTIVE");
        patient.setCreatedAt(LocalDateTime.now());

        if (request.userId() != null) {
            User user = userRepository.findById(request.userId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            patient.setUser(user);
        }

        Patient saved = patientRepository.save(patient);
        return mapToDto(saved);
    }

    @Transactional
    public PatientResponseDto updatePatient(Long id, PatientRequestDto request) {

        Patient existing = patientRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Patient not found with ID: " + id));

        existing.setDob(request.dob());
        existing.setGender(request.gender());
        existing.setContactInfoJson(request.contactInfoJson());
        existing.setAddressJson(request.addressJson());
        existing.setPrimaryContact(request.primaryContact());
        existing.setInsuranceId(request.insuranceId());
        existing.setStatus(request.status());

        if (request.userId() != null) {
            User user = userRepository.findById(request.userId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            existing.setUser(user);
        }

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
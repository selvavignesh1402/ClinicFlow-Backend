package com.HospitalManagement.service;

import com.HospitalManagement.entity.Patient;
import com.HospitalManagement.repository.PatientRepository;
import com.HospitalManagement.requestdto.PatientRequestDto;
import com.HospitalManagement.responsedto.PatientResponseDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PatientService {

    private static final Logger logger = LoggerFactory.getLogger(PatientService.class);
    private static final String ACTIVE = "ACTIVE";

    private final PatientRepository patientRepository;

    public List<PatientResponseDto> getAllPatients() {
        logger.debug("Fetching all patients from registry");
        List<PatientResponseDto> patients = patientRepository.findAll()
                .stream()
                .map(this::toResponseDto)
                .toList();
        logger.info("Retrieved {} patients from registry", patients.size());
        return patients;
    }

    public PatientResponseDto getPatientById(Long id) {
        logger.debug("Fetching patient with ID: {}", id);
        PatientResponseDto patient = toResponseDto(findPatient(id));
        logger.info("Retrieved patient - ID: {}, MRN: {}", id, patient.mrn());
        return patient;
    }

    public PatientResponseDto getPatientByMrn(String mrn) {
        logger.debug("Fetching patient with MRN: {}", mrn);
        Patient patient = patientRepository.findByMrn(mrn.trim().toUpperCase(Locale.ROOT))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found with MRN: " + mrn));
        logger.info("Retrieved patient - MRN: {}, Name: {}", mrn, patient.getName());
        return toResponseDto(patient);
    }

    @Transactional
    public PatientResponseDto registerPatient(PatientRequestDto request) {
        logger.info("Starting patient registration - Name: {}", request.name());
        Patient patient = new Patient();
        patient.setMrn(resolveMrn());
        patient.setName(request.name().trim());
        patient.setDob(request.dob());
        patient.setGender(request.gender().trim().toUpperCase(Locale.ROOT));
        patient.setContactInfoJson(request.contactInfoJson());
        patient.setAddressJson(request.addressJson());
        patient.setPrimaryContact(request.primaryContact().trim());
        patient.setInsuranceId(blankToNull(request.insuranceId()));
        patient.setStatus(resolveStatus(request.status(), ACTIVE));

        Patient savedPatient = patientRepository.save(patient);
        logger.info("Successfully registered patient - ID: {}, MRN: {}", savedPatient.getPatientId(), savedPatient.getMrn());
        return toResponseDto(savedPatient);
    }

    @Transactional
    public PatientResponseDto updatePatient(Long id, PatientRequestDto request) {
        logger.info("Starting patient update - ID: {}", id);
        Patient patient = findPatient(id);

//        if (request.mrn() != null && !request.mrn().isBlank() && !request.mrn().equalsIgnoreCase(patient.getMrn())) {
//            if (patientRepository.existsByMrn(request.mrn().trim().toUpperCase(Locale.ROOT))) {
//                logger.warn("Attempted to update patient MRN to existing value - ID: {}, MRN: {}", id, request.mrn());
//                throw new ResponseStatusException(HttpStatus.CONFLICT, "MRN already exists: " + request.mrn());
//            }
//            patient.setMrn(request.mrn().trim().toUpperCase(Locale.ROOT));
//        }

        patient.setName(request.name().trim());
        patient.setDob(request.dob());
        patient.setGender(request.gender().trim().toUpperCase(Locale.ROOT));
        patient.setContactInfoJson(request.contactInfoJson());
        patient.setAddressJson(request.addressJson());
        patient.setPrimaryContact(request.primaryContact().trim());
        patient.setInsuranceId(blankToNull(request.insuranceId()));
        patient.setStatus(resolveStatus(request.status(), patient.getStatus()));

        Patient updatedPatient = patientRepository.save(patient);
        logger.info("Successfully updated patient - ID: {}, MRN: {}", id, updatedPatient.getMrn());
        return toResponseDto(updatedPatient);
    }

    @Transactional
    public void deactivatePatient(Long id) {
        logger.info("Starting patient deactivation - ID: {}", id);
        Patient patient = findPatient(id);
        patient.deactivate();
        patientRepository.save(patient);
        logger.info("Successfully deactivated patient - ID: {}, MRN: {}", id, patient.getMrn());
    }

    private Patient findPatient(Long id) {
        logger.debug("Looking up patient by ID: {}", id);
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found with id: " + id));
        logger.debug("Found patient - ID: {}, MRN: {}", id, patient.getMrn());
        return patient;
    }

//    private String resolveMrn(String requestedMrn) {
//        if (requestedMrn != null && !requestedMrn.isBlank()) {
//            String mrn = requestedMrn.trim().toUpperCase(Locale.ROOT);
//            logger.debug("Validating requested MRN: {}", mrn);
//            if (patientRepository.existsByMrn(mrn)) {
//                logger.warn("MRN conflict detected: {}", mrn);
//                throw new ResponseStatusException(HttpStatus.CONFLICT, "MRN already exists: " + mrn);
//            }
//            logger.debug("MRN validated successfully: {}", mrn);
//            return mrn;
//        }
//
//        logger.debug("Generating new MRN");
//        String mrn;
//        do {
//            mrn = "MRN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT);
//        } while (patientRepository.existsByMrn(mrn));
//        logger.debug("Generated unique MRN: {}", mrn);
//        return mrn;
//    }


    private String resolveMrn() {
        String mrn;
        do {
            mrn = "MRN-" + UUID.randomUUID()
                    .toString()
                    .substring(0, 8)
                    .toUpperCase(Locale.ROOT);
        } while (patientRepository.existsByMrn(mrn));
        return mrn;
    }


    private String resolveStatus(String requestedStatus, String fallback) {
        if (requestedStatus == null || requestedStatus.isBlank()) {
            return fallback;
        }
        return requestedStatus.trim().toUpperCase(Locale.ROOT);
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private PatientResponseDto toResponseDto(Patient patient) {
        return new PatientResponseDto(
                patient.getPatientId(),
                patient.getMrn(),
                patient.getName(),
                patient.getDob(),
                patient.getGender(),
                patient.getContactInfoJson(),
                patient.getAddressJson(),
                patient.getPrimaryContact(),
                patient.getInsuranceId(),
                patient.getStatus(),
                patient.getCreatedAt(),
                patient.getUpdatedAt()
        );
    }
}
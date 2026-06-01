package com.HospitalManagement.service;

import com.HospitalManagement.entity.Encounter;
import com.HospitalManagement.entity.MedicationMaster;
import com.HospitalManagement.entity.Patient;
import com.HospitalManagement.entity.Prescription;
import com.HospitalManagement.entity.User;
import com.HospitalManagement.enums.PrescriptionStatus;
import com.HospitalManagement.repository.EncounterRepository;
import com.HospitalManagement.repository.MedicationMasterRepository;
import com.HospitalManagement.repository.PatientRepository;
import com.HospitalManagement.repository.PrescriptionRepository;
import com.HospitalManagement.repository.UserRepository;
import com.HospitalManagement.requestdto.PrescriptionRequestDto;
import com.HospitalManagement.responsedto.PrescriptionResponseDto;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class PrescriptionService {

    private static final Logger logger = LoggerFactory.getLogger(PrescriptionService.class);

    private final PrescriptionRepository prescriptionRepository;
    private final EncounterRepository encounterRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private final MedicationMasterRepository medicationMasterRepository;

    public PrescriptionService(
            PrescriptionRepository prescriptionRepository,
            EncounterRepository encounterRepository,
            PatientRepository patientRepository,
            UserRepository userRepository,
            MedicationMasterRepository medicationMasterRepository
    ) {
        this.prescriptionRepository = prescriptionRepository;
        this.encounterRepository = encounterRepository;
        this.patientRepository = patientRepository;
        this.userRepository = userRepository;
        this.medicationMasterRepository = medicationMasterRepository;
    }

    /* ================= READ ================= */

    @Transactional(readOnly = true)
    public List<PrescriptionResponseDto> getAllPrescriptions() {
        logger.debug("Fetching all prescriptions");
        return prescriptionRepository.findAll()
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public PrescriptionResponseDto getPrescriptionById(Long rxId) {
        logger.debug("Fetching prescription with ID: {}", rxId);
        return toResponseDto(findPrescription(rxId));
    }

    /* ================= CREATE ================= */

    public PrescriptionResponseDto createPrescription(PrescriptionRequestDto requestDto) {
        logger.info("Creating prescription for PatientID={}, EncounterID={}",
                requestDto.patientId(), requestDto.encounterId());

        Prescription prescription = new Prescription();
        mapRequestToEntity(requestDto, prescription, true);

        return toResponseDto(prescriptionRepository.save(prescription));
    }

    /* ================= UPDATE ================= */

    public PrescriptionResponseDto updatePrescription(Long rxId, PrescriptionRequestDto requestDto) {
        logger.info("Updating prescription RxID={}, NewStatus={}", rxId, requestDto.status());

        Prescription prescription = findPrescription(rxId);

        if (prescription.getStatus() == PrescriptionStatus.ISSUED) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Issued prescriptions cannot be modified"
            );
        }

        if (requestDto.status() != null) {
            prescription.setStatus(requestDto.status());
            prescription.setIssuedAt(LocalDateTime.now());
            prescription.setClinician(getAuthenticatedClinician());
        }

        return toResponseDto(prescriptionRepository.save(prescription));
    }

    /* ================= DELETE ================= */

    public void deletePrescription(Long rxId) {
        logger.info("Deleting prescription RxID={}", rxId);
        prescriptionRepository.delete(findPrescription(rxId));
    }

    /* ================= MAPPING ================= */

    private void mapRequestToEntity(
            PrescriptionRequestDto requestDto,
            Prescription prescription,
            boolean isCreate
    ) {
        prescription.setEncounter(findEncounter(requestDto.encounterId()));
        prescription.setPatient(findPatient(requestDto.patientId()));
        prescription.setMedication(findMedication(requestDto.medicationId()));

        prescription.setDosage(requestDto.dosage());
        prescription.setFrequency(requestDto.frequency());
        prescription.setDurationDays(requestDto.durationDays());
        prescription.setQuantity(requestDto.quantity());
        prescription.setRepeats(requestDto.repeats());
        prescription.setRoute(requestDto.route());
        prescription.setNotes(requestDto.notes());

        if (requestDto.status() != null) {
            prescription.setStatus(requestDto.status());
        }

        // ✅ Clinician identity from JWT
        prescription.setClinician(getAuthenticatedClinician());

        // ✅ Server‑controlled issuedAt
        if (isCreate || requestDto.status() == PrescriptionStatus.ISSUED) {
            prescription.setIssuedAt(LocalDateTime.now());
        }
    }

    /* ================= HELPERS ================= */

    private Prescription findPrescription(Long rxId) {
        return prescriptionRepository.findById(rxId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Prescription not found with id: " + rxId));
    }

    private Encounter findEncounter(Long encounterId) {
        return encounterRepository.findById(encounterId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Encounter not found with id: " + encounterId));
    }

    private Patient findPatient(Long patientId) {
        return patientRepository.findById(patientId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Patient not found with id: " + patientId));
    }

    private MedicationMaster findMedication(Long medicationId) {
        return medicationMasterRepository.findById(medicationId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Medication not found with id: " + medicationId));
    }

    private User getAuthenticatedClinician() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Authenticated clinician not found"
                ));
    }

    /* ================= RESPONSE ================= */

    private PrescriptionResponseDto toResponseDto(Prescription prescription) {
        return new PrescriptionResponseDto(
                prescription.getRxId(),
                prescription.getEncounter().getEncounterId(),
                prescription.getPatient().getPatientId(),
                prescription.getPatient().getName(),
                prescription.getClinician().getUserId(),
                prescription.getClinician().getName(),
                prescription.getMedication().getMedId(),
                prescription.getMedication().getName(),
                prescription.getDosage(),
                prescription.getFrequency(),
                prescription.getDurationDays(),
                prescription.getQuantity(),
                prescription.getRepeats(),
                prescription.getRoute(),
                prescription.getNotes(),
                prescription.getStatus(),
                prescription.getIssuedAt()
        );
    }
}
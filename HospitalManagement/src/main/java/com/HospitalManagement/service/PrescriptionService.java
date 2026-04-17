package com.HospitalManagement.service;

import com.HospitalManagement.entity.Encounter;
import com.HospitalManagement.entity.MedicationMaster;
import com.HospitalManagement.entity.Patient;
import com.HospitalManagement.entity.Prescription;
import com.HospitalManagement.entity.User;
import com.HospitalManagement.repository.EncounterRepository;
import com.HospitalManagement.repository.MedicationMasterRepository;
import com.HospitalManagement.repository.PatientRepository;
import com.HospitalManagement.repository.PrescriptionRepository;
import com.HospitalManagement.repository.UserRepository;
import com.HospitalManagement.requestdto.PrescriptionRequestDto;
import com.HospitalManagement.responsedto.PrescriptionResponseDto;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class PrescriptionService {

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

    @Transactional(readOnly = true)
    public List<PrescriptionResponseDto> getAllPrescriptions() {
        return prescriptionRepository.findAll()
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public PrescriptionResponseDto getPrescriptionById(Long rxId) {
        return toResponseDto(findPrescription(rxId));
    }

    public PrescriptionResponseDto createPrescription(PrescriptionRequestDto requestDto) {
        Prescription prescription = new Prescription();
        mapRequestToEntity(requestDto, prescription);
        if (prescription.getIssuedAt() == null) {
            prescription.setIssuedAt(LocalDateTime.now());
        }
        return toResponseDto(prescriptionRepository.save(prescription));
    }

    public PrescriptionResponseDto updatePrescription(Long rxId, PrescriptionRequestDto requestDto) {
        Prescription prescription = findPrescription(rxId);
        mapRequestToEntity(requestDto, prescription);
        if (prescription.getIssuedAt() == null) {
            prescription.setIssuedAt(LocalDateTime.now());
        }
        return toResponseDto(prescriptionRepository.save(prescription));
    }

    public void deletePrescription(Long rxId) {
        Prescription prescription = findPrescription(rxId);
        prescriptionRepository.delete(prescription);
    }

    private void mapRequestToEntity(PrescriptionRequestDto requestDto, Prescription prescription) {
        prescription.setEncounter(findEncounter(requestDto.encounterId()));
        prescription.setPatient(findPatient(requestDto.patientId()));
        prescription.setClinician(findUser(requestDto.clinicianId()));
        prescription.setMedication(findMedication(requestDto.medicationId()));
        prescription.setDosage(requestDto.dosage());
        prescription.setFrequency(requestDto.frequency());
        prescription.setDurationDays(requestDto.durationDays());
        prescription.setQuantity(requestDto.quantity());
        prescription.setRepeats(requestDto.repeats());
        prescription.setRoute(requestDto.route());
        prescription.setNotes(requestDto.notes());
        prescription.setStatus(requestDto.status());
        prescription.setIssuedAt(requestDto.issuedAt());
    }

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

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found with id: " + userId));
    }

    private MedicationMaster findMedication(Long medicationId) {
        return medicationMasterRepository.findById(medicationId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Medication not found with id: " + medicationId));
    }

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

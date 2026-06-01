package com.HospitalManagement.service;

import com.HospitalManagement.entity.Encounter;
import com.HospitalManagement.entity.Patient;
import com.HospitalManagement.entity.User;
import com.HospitalManagement.enums.EncounterStatus;
import com.HospitalManagement.enums.Roles;
import com.HospitalManagement.repository.EncounterRepository;
import com.HospitalManagement.repository.PatientRepository;
import com.HospitalManagement.repository.UserRepository;
import com.HospitalManagement.requestdto.EncounterRequestDto;
import com.HospitalManagement.responsedto.EncounterResponseDto;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class EncounterService {

    private static final Logger logger = LoggerFactory.getLogger(EncounterService.class);

    private final EncounterRepository encounterRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;

    public EncounterService(
            EncounterRepository encounterRepository,
            PatientRepository patientRepository,
            UserRepository userRepository
    ) {
        this.encounterRepository = encounterRepository;
        this.patientRepository = patientRepository;
        this.userRepository = userRepository;
    }

    // READ //

    @Transactional(readOnly = true)
    public List<EncounterResponseDto> getAllEncounters() {
        logger.debug("Fetching all encounters");
        List<Encounter> encounters = encounterRepository.findAll();
        List<EncounterResponseDto> responseDtos = new ArrayList<>();

        for (Encounter encounter : encounters) {
            responseDtos.add(toResponseDto(encounter));
        }

        logger.info("Retrieved {} encounters", responseDtos.size());
        return responseDtos;
    }

    @Transactional(readOnly = true)
    public EncounterResponseDto getEncounterById(Long encounterId) {
        logger.debug("Fetching encounter with ID: {}", encounterId);
        EncounterResponseDto response = toResponseDto(findEncounter(encounterId));
        logger.info("Retrieved encounter - ID: {}", encounterId);
        return response;
    }

    // CREATE //

    public EncounterResponseDto createEncounter(EncounterRequestDto requestDto) {
        logger.info("Creating new encounter - PatientID: {}, VisitType: {}", requestDto.patientId(), requestDto.visitType());
        User clinician = getAuthenticatedClinician();

        Encounter encounter = new Encounter();
        encounter.setPatient(findPatient(requestDto.patientId()));
        encounter.setClinician(clinician);
        encounter.setVisitType(requestDto.visitType());
        encounter.setChiefComplaint(requestDto.chiefComplaint());
        encounter.setVitalsJson(requestDto.vitalsJson());
        encounter.setNotesJson(requestDto.notesJson());
        encounter.setDiagnosesJson(requestDto.diagnosesJson());
        encounter.setOrdersJson(requestDto.ordersJson());
        encounter.setPrescriptionsJson(requestDto.prescriptionsJson());
        encounter.setStartAt( LocalDateTime.now());
        encounter.setStatus(EncounterStatus.IN_PROGRESS);

        Encounter saved = encounterRepository.save(encounter);
        logger.info("Encounter created successfully - ID: {}", saved.getEncounterId());
        return toResponseDto(saved);
    }

    // UPDATE / COMPLETE //

    public EncounterResponseDto updateEncounter(Long encounterId, EncounterRequestDto requestDto) {
        logger.info("Updating encounter - ID: {}", encounterId);
        Encounter encounter = findEncounter(encounterId);
        User clinician = getAuthenticatedClinician();

        if (encounter.getStatus() == EncounterStatus.COMPLETED) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Completed encounters cannot be modified"
            );
        }

        encounter.setVisitType(requestDto.visitType());
        encounter.setChiefComplaint(requestDto.chiefComplaint());
        encounter.setVitalsJson(requestDto.vitalsJson());
        encounter.setNotesJson(requestDto.notesJson());
        encounter.setDiagnosesJson(requestDto.diagnosesJson());
        encounter.setOrdersJson(requestDto.ordersJson());
        encounter.setPrescriptionsJson(requestDto.prescriptionsJson());

        if (requestDto.status() == EncounterStatus.COMPLETED) {
            encounter.setStatus(EncounterStatus.COMPLETED);
            encounter.setEndAt(LocalDateTime.now());
            encounter.setSignedBy(clinician);
            encounter.setSignedAt(LocalDateTime.now());
        }

        Encounter updated = encounterRepository.save(encounter);
        logger.info("Encounter updated successfully - ID: {}", encounterId);
        return toResponseDto(updated);
    }

    public EncounterResponseDto completeEncounter(Long encounterId) {
        logger.info("Completing encounter - ID: {}", encounterId);
        Encounter encounter = findEncounter(encounterId);
        User clinician = getAuthenticatedClinician();

        if (encounter.getStatus() == EncounterStatus.COMPLETED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Encounter is already completed");
        }

        if (encounter.getStatus() != EncounterStatus.IN_PROGRESS) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only IN_PROGRESS encounters can be completed");
        }

        encounter.setStatus(EncounterStatus.COMPLETED);
        encounter.setEndAt(LocalDateTime.now());
        encounter.setSignedBy(clinician);
        encounter.setSignedAt(LocalDateTime.now());

        Encounter saved = encounterRepository.save(encounter);
        logger.info("Encounter completed successfully - ID: {}", encounterId);
        return toResponseDto(saved);
    }

    // DELETE //

    public void deleteEncounter(Long encounterId) {
        logger.info("Deleting encounter - ID: {}", encounterId);
        encounterRepository.delete(findEncounter(encounterId));
        logger.info("Encounter deleted successfully - ID: {}", encounterId);
    }

    // HELPERS //

    private User getAuthenticatedClinician() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Authenticated user not found"
                ));

        if (user.getRole() != Roles.CLINICIAN) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only CLINICIAN users are allowed to perform this action"
            );
        }
        return user;
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

    private EncounterResponseDto toResponseDto(Encounter encounter) {
        User signedBy = encounter.getSignedBy();
        return new EncounterResponseDto(
                encounter.getEncounterId(),
                encounter.getPatient().getPatientId(),
                encounter.getPatient().getName(),
                encounter.getClinician().getUserId(),
                encounter.getClinician().getName(),
                encounter.getVisitType(),
                encounter.getChiefComplaint(),
                encounter.getVitalsJson(),
                encounter.getNotesJson(),
                encounter.getDiagnosesJson(),
                encounter.getOrdersJson(),
                encounter.getPrescriptionsJson(),
                encounter.getStartAt(),
                encounter.getEndAt(),
                encounter.getStatus(),
                signedBy != null ? signedBy.getUserId() : null,
                signedBy != null ? signedBy.getName() : null,
                encounter.getSignedAt()
        );
    }
}
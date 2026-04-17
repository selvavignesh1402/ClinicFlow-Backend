package com.HospitalManagement.service;

import com.HospitalManagement.entity.Encounter;
import com.HospitalManagement.entity.Patient;
import com.HospitalManagement.entity.User;
import com.HospitalManagement.repository.EncounterRepository;
import com.HospitalManagement.repository.PatientRepository;
import com.HospitalManagement.repository.UserRepository;
import com.HospitalManagement.requestdto.EncounterRequestDto;
import com.HospitalManagement.responsedto.EncounterResponseDto;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class EncounterService {

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

    @Transactional(readOnly = true)
    public List<EncounterResponseDto> getAllEncounters() {
        return encounterRepository.findAll()
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public EncounterResponseDto getEncounterById(Long encounterId) {
        return toResponseDto(findEncounter(encounterId));
    }

    public EncounterResponseDto createEncounter(EncounterRequestDto requestDto) {
        Encounter encounter = new Encounter();
        mapRequestToEntity(requestDto, encounter);
        return toResponseDto(encounterRepository.save(encounter));
    }

    public EncounterResponseDto updateEncounter(Long encounterId, EncounterRequestDto requestDto) {
        Encounter encounter = findEncounter(encounterId);
        mapRequestToEntity(requestDto, encounter);
        return toResponseDto(encounterRepository.save(encounter));
    }

    public void deleteEncounter(Long encounterId) {
        Encounter encounter = findEncounter(encounterId);
        encounterRepository.delete(encounter);
    }

    private void mapRequestToEntity(EncounterRequestDto requestDto, Encounter encounter) {
        encounter.setPatient(findPatient(requestDto.patientId()));
        encounter.setClinician(findUser(requestDto.clinicianId()));
        encounter.setVisitType(requestDto.visitType());
        encounter.setChiefComplaint(requestDto.chiefComplaint());
        encounter.setVitalsJson(requestDto.vitalsJson());
        encounter.setNotesJson(requestDto.notesJson());
        encounter.setDiagnosesJson(requestDto.diagnosesJson());
        encounter.setOrdersJson(requestDto.ordersJson());
        encounter.setPrescriptionsJson(requestDto.prescriptionsJson());
        encounter.setStartAt(requestDto.startAt());
        encounter.setEndAt(requestDto.endAt());
        encounter.setStatus(requestDto.status());
        encounter.setSignedBy(requestDto.signedById() != null ? findUser(requestDto.signedById()) : null);
        encounter.setSignedAt(requestDto.signedAt());
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

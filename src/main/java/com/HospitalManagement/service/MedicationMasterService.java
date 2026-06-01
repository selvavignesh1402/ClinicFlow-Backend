package com.HospitalManagement.service;

import com.HospitalManagement.entity.MedicationMaster;
import com.HospitalManagement.enums.MedicationStatus;
import com.HospitalManagement.repository.MedicationMasterRepository;
import com.HospitalManagement.requestdto.PharmacyRequestDto;
import com.HospitalManagement.responsedto.MedicationResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class MedicationMasterService {

    private static final Logger logger = LoggerFactory.getLogger(MedicationMasterService.class);

    private final MedicationMasterRepository medicationRepository;

    public MedicationMasterService(MedicationMasterRepository medicationRepository) {
        this.medicationRepository = medicationRepository;
    }

    @Transactional(readOnly = true)
    public List<MedicationResponseDto> getAllMedications(String search) {
        logger.debug("Fetching medications - Search: {}", search);
        List<MedicationMaster> medicines = search == null || search.isBlank()
                ? medicationRepository.findAll()
                : medicationRepository.findByNameContainingIgnoreCaseOrCodeContainingIgnoreCase(search, search);

        List<MedicationResponseDto> response = new ArrayList<>();
        for (MedicationMaster medicine : medicines) {
            response.add(toDto(medicine));
        }
        logger.info("Retrieved {} medications", response.size());
        return response;
    }

    @Transactional(readOnly = true)
    public MedicationResponseDto getMedicationById(Long medId) {
        logger.debug("Fetching medication by ID: {}", medId);
        MedicationResponseDto medication = toDto(findMedication(medId));
        logger.info("Retrieved medication - ID: {}, Name: {}", medId, medication.name());
        return medication;
    }

    public MedicationResponseDto createMedication(PharmacyRequestDto request) {
        logger.info("Creating medication - Code: {}, Name: {}", request.code(), request.name());
        if (medicationRepository.existsByCode(request.code())) {
            logger.warn("Attempted to create medication with existing code: {}", request.code());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Medication code already exists");
        }

        MedicationMaster medication = new MedicationMaster();
        medication.setCode(request.code());
        medication.setName(request.name());
        medication.setFormulation(request.formulation());
        medication.setStrength(request.strength());
        medication.setAtcCode(request.atcCode());
        medication.setControlledFlag(request.controlledFlag());

        // ✅ default
        medication.setStatus(MedicationStatus.ACTIVE);

        MedicationMaster savedMedication = medicationRepository.save(medication);
        logger.info("Successfully created medication - ID: {}, Code: {}", savedMedication.getMedId(), request.code());
        return toDto(savedMedication);
    }

    public MedicationResponseDto updateMedication(Long medId, PharmacyRequestDto request) {
        logger.info("Updating medication - ID: {}", medId);
        MedicationMaster medication = findMedication(medId);
        MedicationMaster sameCode = medicationRepository.findByCode(request.code()).orElse(null);
        if (sameCode != null && !sameCode.getMedId().equals(medId)) {
            logger.warn("Attempted to update medication with existing code: {}", request.code());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Medication code already exists");
        }
        copyRequest(request, medication);
        MedicationMaster updatedMedication = medicationRepository.save(medication);
        logger.info("Successfully updated medication - ID: {}", medId);
        return toDto(updatedMedication);
    }

    public void deleteMedication(Long medId) {
        logger.info("Deactivating medication - ID: {}", medId);
        MedicationMaster medication = findMedication(medId);
        medication.setStatus(MedicationStatus.INACTIVE);
        medicationRepository.save(medication);
        logger.info("Successfully deactivated medication - ID: {}", medId);
    }

    private MedicationMaster findMedication(Long medId) {
        logger.debug("Looking up medication by ID: {}", medId);
        MedicationMaster medication = medicationRepository.findById(medId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Medication not found"));
        logger.debug("Found medication - ID: {}, Name: {}", medId, medication.getName());
        return medication;
    }

    private void copyRequest(PharmacyRequestDto request, MedicationMaster medication) {
        medication.setCode(request.code());
        medication.setName(request.name());
        medication.setFormulation(request.formulation());
        medication.setStrength(request.strength());
        medication.setAtcCode(request.atcCode());
        medication.setControlledFlag(request.controlledFlag());
    }

    private MedicationResponseDto toDto(MedicationMaster medication) {
        return new MedicationResponseDto(
                medication.getMedId(),
                medication.getCode(),
                medication.getName(),
                medication.getFormulation(),
                medication.getStrength(),
                medication.getAtcCode(),
                medication.getControlledFlag(),
                medication.getStatus()
        );
    }
}
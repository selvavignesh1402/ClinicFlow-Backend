package com.HospitalManagement.service;

import com.HospitalManagement.entity.DispenseRecord;
import com.HospitalManagement.entity.InventoryItem;
import com.HospitalManagement.entity.Patient;
import com.HospitalManagement.entity.Prescription;
import com.HospitalManagement.entity.User;
import com.HospitalManagement.enums.DispenseStatus;
import com.HospitalManagement.enums.InventoryStatus;
import com.HospitalManagement.enums.MedicationStatus;
import com.HospitalManagement.enums.PrescriptionStatus;
import com.HospitalManagement.repository.DispenseRecordRepository;
import com.HospitalManagement.repository.InventoryItemRepository;
import com.HospitalManagement.repository.PrescriptionRepository;
import com.HospitalManagement.repository.UserRepository;
import com.HospitalManagement.requestdto.PharmacyRequestDto;
import com.HospitalManagement.responsedto.DispenseResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class PharmacyService {

    private static final Logger logger = LoggerFactory.getLogger(PharmacyService.class);

    private final DispenseRecordRepository dispenseRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final InventoryItemRepository inventoryRepository;
    private final UserRepository userRepository;
    private final InventoryService inventoryService;

    public PharmacyService(
            DispenseRecordRepository dispenseRepository,
            PrescriptionRepository prescriptionRepository,
            InventoryItemRepository inventoryRepository,
            UserRepository userRepository,
            InventoryService inventoryService
    ) {
        this.dispenseRepository = dispenseRepository;
        this.prescriptionRepository = prescriptionRepository;
        this.inventoryRepository = inventoryRepository;
        this.userRepository = userRepository;
        this.inventoryService = inventoryService;
    }

    @Transactional(readOnly = true)
    public List<DispenseResponseDto> getAllDispenseRecords() {
        logger.debug("Fetching all dispense records");
        List<DispenseResponseDto> records = toDtos(dispenseRepository.findAll());
        logger.info("Retrieved {} dispense records", records.size());
        return records;
    }

    @Transactional(readOnly = true)
    public DispenseResponseDto getDispenseRecordById(Long dispenseId) {
        logger.debug("Fetching dispense record by ID: {}", dispenseId);
        DispenseResponseDto record = toDto(findDispenseRecord(dispenseId));
        logger.info("Retrieved dispense record - ID: {}, Quantity: {}", dispenseId, record.quantity());
        return record;
    }

    @Transactional(readOnly = true)
    public List<DispenseResponseDto> getDispenseRecordsByPrescription(Long prescriptionId) {
        logger.debug("Fetching dispense records for prescription ID: {}", prescriptionId);
        List<DispenseResponseDto> records = toDtos(dispenseRepository.findByPrescriptionRxId(prescriptionId));
        logger.info("Retrieved {} dispense records for prescription ID: {}", records.size(), prescriptionId);
        return records;
    }

    public DispenseResponseDto dispensePrescription(PharmacyRequestDto request) {
        logger.info("Dispensing prescription - Prescription ID: {}, Quantity: {}", request.prescriptionId(), request.quantity());
        Prescription prescription = findPrescription(request.prescriptionId());
        User dispensedBy = findUser(request.dispensedById());
        InventoryItem item = request.inventoryItemId() == null
                ? findAvailableBatch(prescription, request.quantity())
                : inventoryService.findInventoryItem(request.inventoryItemId());

        validateDispense(prescription, item, request.quantity());

        item.setQuantity(item.getQuantity() - request.quantity());
        if (item.getQuantity() == 0) {
            item.setStatus(InventoryStatus.OUT_OF_STOCK);
        }
        inventoryRepository.save(item);

        DispenseRecord record = new DispenseRecord();
        record.setPrescription(prescription);
        record.setInventoryItem(item);
        record.setPatient(prescription.getPatient());
        record.setDispensedBy(dispensedBy);
        record.setQuantity(request.quantity());
        record.setDispensedAt(LocalDateTime.now());
        record.setNotes(request.notes());
        record.setStatus(DispenseStatus.DISPENSED);

        prescription.setStatus(PrescriptionStatus.DISPENSED);
        prescriptionRepository.save(prescription);
        DispenseRecord savedRecord = dispenseRepository.save(record);
        logger.info("Successfully dispensed prescription - Dispense ID: {}, Prescription ID: {}", savedRecord.getDispenseId(), request.prescriptionId());
        return toDto(savedRecord);
    }

    public DispenseResponseDto returnDispense(Long dispenseId, PharmacyRequestDto request) {
        logger.info("Processing dispense return - Dispense ID: {}, Return Quantity: {}", dispenseId, request.quantity());
        DispenseRecord record = findDispenseRecord(dispenseId);
        if (record.getStatus() != DispenseStatus.DISPENSED) {
            logger.warn("Attempted return of non-dispensed record - ID: {}, Status: {}", dispenseId, record.getStatus());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only dispensed records can be returned");
        }
        if (request.quantity() > record.getQuantity()) {
            logger.warn("Return quantity exceeds dispensed quantity - Dispense ID: {}, Requested: {}, Dispensed: {}", dispenseId, request.quantity(), record.getQuantity());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Return quantity is too high");
        }

        InventoryItem item = record.getInventoryItem();
        item.setQuantity(item.getQuantity() + request.quantity());
        if (item.getStatus() == InventoryStatus.OUT_OF_STOCK) {
            item.setStatus(InventoryStatus.IN_STOCK);
        }
        inventoryRepository.save(item);

        record.setStatus(request.quantity().equals(record.getQuantity()) ? DispenseStatus.RETURNED : DispenseStatus.PARTIALLY_RETURNED);
        DispenseRecord updatedRecord = dispenseRepository.save(record);
        logger.info("Successfully processed dispense return - Dispense ID: {}, Status: {}", dispenseId, updatedRecord.getStatus());
        return toDto(updatedRecord);
    }

    private void validateDispense(Prescription prescription, InventoryItem item, Integer quantity) {
        logger.debug("Validating dispense - Prescription ID: {}, Item ID: {}, Quantity: {}", prescription.getRxId(), item.getInventoryId(), quantity);
        if (prescription.getStatus() != PrescriptionStatus.ISSUED) {
            logger.warn("Prescription not in issued status - ID: {}, Status: {}", prescription.getRxId(), prescription.getStatus());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Prescription is not issued");
        }
        if (quantity > prescription.getQuantity()) {
            logger.warn("Dispense quantity exceeds prescription quantity - Requested: {}, Prescribed: {}", quantity, prescription.getQuantity());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantity is more than prescription quantity");
        }
        if (!item.getMedication().getMedId().equals(prescription.getMedication().getMedId())) {
            logger.warn("Medication mismatch - Prescription Med ID: {}, Inventory Med ID: {}", prescription.getMedication().getMedId(), item.getMedication().getMedId());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Medicine does not match prescription");
        }
        if (!inventoryService.isDispensableBatch(item) || item.getQuantity() < quantity) {
            logger.warn("Insufficient stock - Item ID: {}, Available: {}, Requested: {}", item.getInventoryId(), item.getQuantity(), quantity);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Stock is not available");
        }
        if (item.getMedication().getStatus() != MedicationStatus.ACTIVE) {
            logger.warn("Inactive medication - Med ID: {}, Status: {}", item.getMedication().getMedId(), item.getMedication().getStatus());
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Medication is inactive"
            );
        }
        logger.debug("Dispense validation passed");
    }

    private InventoryItem findAvailableBatch(Prescription prescription, Integer quantity) {
        logger.debug("Finding available batch for prescription - Med ID: {}, Quantity: {}", prescription.getMedication().getMedId(), quantity);
        InventoryItem selected = null;
        for (InventoryItem item : inventoryRepository.findByMedicationMedId(prescription.getMedication().getMedId())) {
            if (inventoryService.isDispensableBatch(item) && item.getQuantity() >= quantity) {
                if (selected == null || item.getExpiryDate().isBefore(selected.getExpiryDate())) {
                    selected = item;
                }
            }
        }
        if (selected == null) {
            logger.warn("No available batch found for medication - Med ID: {}, Quantity: {}", prescription.getMedication().getMedId(), quantity);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No stock available");
        }
        logger.debug("Selected batch for dispense - Item ID: {}, Expiry: {}", selected.getInventoryId(), selected.getExpiryDate());
        return selected;
    }

    private Prescription findPrescription(Long prescriptionId) {
        logger.debug("Looking up prescription by ID: {}", prescriptionId);
        Prescription prescription = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Prescription not found"));
        logger.debug("Found prescription - ID: {}, Status: {}", prescriptionId, prescription.getStatus());
        return prescription;
    }

    private User findUser(Long userId) {
        logger.debug("Looking up user by ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        logger.debug("Found user - ID: {}, Username: {}", userId, user.getUsername());
        return user;
    }

    private DispenseRecord findDispenseRecord(Long dispenseId) {
        logger.debug("Looking up dispense record by ID: {}", dispenseId);
        DispenseRecord record = dispenseRepository.findById(dispenseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dispense record not found"));
        logger.debug("Found dispense record - ID: {}, Status: {}", dispenseId, record.getStatus());
        return record;
    }

    private List<DispenseResponseDto> toDtos(List<DispenseRecord> records) {
        List<DispenseResponseDto> response = new ArrayList<>();
        for (DispenseRecord record : records) {
            response.add(toDto(record));
        }
        return response;
    }

    private DispenseResponseDto toDto(DispenseRecord record) {
        InventoryItem item = record.getInventoryItem();
        Patient patient = record.getPatient();
        User dispensedBy = record.getDispensedBy();

        return new DispenseResponseDto(
                record.getDispenseId(),
                record.getPrescription().getRxId(),
                item.getInventoryId(),
                item.getMedication().getName(),
                item.getBatchNumber(),
                patient.getPatientId(),
                patient == null ? null : patient.getName(),
                dispensedBy == null ? null : dispensedBy.getUserId(),
                dispensedBy == null ? null : dispensedBy.getName(),
                record.getQuantity(),
                record.getDispensedAt(),
                record.getStatus().name()
        );
    }
}
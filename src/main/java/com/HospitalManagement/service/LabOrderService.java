package com.HospitalManagement.service;

import com.HospitalManagement.entity.Encounter;
import com.HospitalManagement.entity.LabOrder;
import com.HospitalManagement.entity.LabResult;
import com.HospitalManagement.entity.Patient;
import com.HospitalManagement.entity.User;
import com.HospitalManagement.repository.EncounterRepository;
import com.HospitalManagement.repository.LabOrderRepository;
import com.HospitalManagement.repository.LabResultRepository;
import com.HospitalManagement.repository.PatientRepository;
import com.HospitalManagement.repository.UserRepository;
import com.HospitalManagement.requestdto.LabOrderRequestDto;
import com.HospitalManagement.responsedto.LabOrderResponseDto;
import com.HospitalManagement.responsedto.LabResultSummaryDto;
import com.HospitalManagement.service.AuthService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.HospitalManagement.enums.LabOrderStatus;
@Service
@Transactional
public class LabOrderService {

    private static final Logger logger = LoggerFactory.getLogger(LabOrderService.class);

    private final LabOrderRepository labOrderRepository;
    private final LabResultRepository labResultRepository;
    private final EncounterRepository encounterRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private final AuthService authService;

    public LabOrderService(LabOrderRepository labOrderRepository,
                           LabResultRepository labResultRepository,
                           EncounterRepository encounterRepository,
                           PatientRepository patientRepository,
                           UserRepository userRepository,
                           AuthService authService) {

        this.labOrderRepository = labOrderRepository;
        this.labResultRepository = labResultRepository;
        this.encounterRepository = encounterRepository;
        this.patientRepository = patientRepository;
        this.userRepository = userRepository;
        this.authService = authService;
    }

    @Transactional(readOnly = true)
    public List<LabOrderResponseDto> getAllOrders() {
        logger.debug("Fetching all lab orders");
        List<LabOrder> labOrders = labOrderRepository.findAll();
        List<LabOrderResponseDto> orders = toOrderResponseList(labOrders);
        logger.info("Retrieved {} lab orders", orders.size());
        return orders;
    }

    @Transactional(readOnly = true)
    public LabOrderResponseDto getOrderById(Long labOrderId) {
        logger.debug("Fetching lab order by ID: {}", labOrderId);
        LabOrderResponseDto order = toOrderResponseDto(findLabOrder(labOrderId));
        logger.info("Retrieved lab order - ID: {}, Status: {}", labOrderId, order.status());
        return order;
    }

    @Transactional(readOnly = true)
    public List<LabOrderResponseDto> getOrdersByPatientId(Long patientId) {
        logger.debug("Fetching lab orders for patient ID: {}", patientId);
        findPatient(patientId);
        List<LabOrder> labOrders = labOrderRepository.findAllByPatientPatientId(patientId);
        List<LabOrderResponseDto> orders = toOrderResponseList(labOrders);
        logger.info("Retrieved {} lab orders for patient ID: {}", orders.size(), patientId);
        return orders;
    }

    public LabOrderResponseDto createOrder(LabOrderRequestDto requestDto) {

        logger.info("Creating lab order - Encounter ID: {}", requestDto.encounterId());

        User currentUser = authService.getAuthenticatedUser();

        Encounter encounter = findEncounter(requestDto.encounterId());

        LabOrder labOrder = new LabOrder();

        labOrder.setEncounter(encounter);
        labOrder.setPatient(encounter.getPatient());   // ✅ from encounter
        labOrder.setOrderedBy(currentUser);            // ✅ logged-in user
        labOrder.setTestsJson(requestDto.testsJson());
        labOrder.setSampleId(resolveSampleId(requestDto.sampleId()));
        labOrder.setCollectedAt(requestDto.collectedAt());
        labOrder.setStatus(LabOrderStatus.ORDERED);

        LabOrder savedOrder = labOrderRepository.save(labOrder);

        if (savedOrder.getResultUri() == null || savedOrder.getResultUri().isBlank()) {
            savedOrder.setResultUri(buildResultUri(savedOrder.getLabOrderId()));
            savedOrder = labOrderRepository.save(savedOrder);
        }

        logger.info("Successfully created lab order - ID: {}", savedOrder.getLabOrderId());

        return toOrderResponseDto(savedOrder);
    }

    public LabOrderResponseDto updateOrder(Long labOrderId, LabOrderRequestDto requestDto) {
        logger.info("Updating lab order - ID: {}", labOrderId);
        LabOrder labOrder = findLabOrder(labOrderId);

        if (labOrder.getStatus() != LabOrderStatus.ORDERED) {
            logger.warn("Attempted to update lab order not in ORDERED status - ID: {}, Status: {}", labOrderId, labOrder.getStatus());
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Lab order cannot be modified after sample collection"
            );
        }

        // Only allow updating tests before collection
        labOrder.setTestsJson(requestDto.testsJson());
        LabOrder updatedOrder = labOrderRepository.save(labOrder);
        logger.info("Successfully updated lab order - ID: {}", labOrderId);
        return toOrderResponseDto(updatedOrder);
    }

    public LabOrderResponseDto cancelOrder(Long labOrderId) {
        logger.info("Cancelling lab order - ID: {}", labOrderId);
        LabOrder labOrder = findLabOrder(labOrderId);

        if (labOrder.getStatus() == LabOrderStatus.RESULTS_REPORTED ||
                labOrder.getStatus() == LabOrderStatus.CRITICAL_REPORTED) {

            logger.warn("Attempted to cancel lab order with reported results - ID: {}, Status: {}", labOrderId, labOrder.getStatus());
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cannot cancel a lab order with reported results"
            );
        }

        labOrder.setStatus(LabOrderStatus.CANCELLED);
        LabOrder cancelledOrder = labOrderRepository.save(labOrder);
        logger.info("Successfully cancelled lab order - ID: {}", labOrderId);
        return toOrderResponseDto(cancelledOrder);
    }

    public LabOrderResponseDto collectSample(Long labOrderId) {
        logger.info("Collecting sample for lab order - ID: {}", labOrderId);
        LabOrder labOrder = findLabOrder(labOrderId);

        if (labOrder.getStatus() != LabOrderStatus.ORDERED) {
            logger.warn("Attempted to collect sample for non-ordered lab order - ID: {}, Status: {}", labOrderId, labOrder.getStatus());
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Sample can only be collected once"
            );
        }

        labOrder.setCollectedAt(LocalDateTime.now());
        labOrder.setStatus(LabOrderStatus.COLLECTED);
        LabOrder collectedOrder = labOrderRepository.save(labOrder);
        logger.info("Successfully collected sample for lab order - ID: {}", labOrderId);
        return toOrderResponseDto(collectedOrder);
    }


    private void mapRequestToEntity(LabOrderRequestDto requestDto, LabOrder labOrder) {

        labOrder.setEncounter(findEncounter(requestDto.encounterId()));

        labOrder.setTestsJson(requestDto.testsJson());
        labOrder.setSampleId(resolveSampleId(requestDto.sampleId()));
        labOrder.setCollectedAt(requestDto.collectedAt());

        if (labOrder.getLabOrderId() == null) {
            labOrder.setStatus(LabOrderStatus.ORDERED);
        }
    }

//helper functions
    private String resolveSampleId(String requestedSampleId) {
        if (requestedSampleId == null || requestedSampleId.isBlank()) {
            return generateSampleId();
        }
        return requestedSampleId;
    }

    private LabOrderStatus resolveOrderStatus(LabOrderStatus requestedStatus, LocalDateTime collectedAt) {
        if (requestedStatus == null) {
            return collectedAt != null ? LabOrderStatus.COLLECTED: LabOrderStatus.ORDERED;
        }
        return requestedStatus;
    }

    private String generateSampleId() {
        return "SMP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private String buildResultUri(Long labOrderId) {
        if (labOrderId == null) {
            return null;
        }
        return "/api/v1/lab/orders/" + labOrderId + "/results";
    }

    private LabOrder findLabOrder(Long labOrderId) {
        logger.debug("Looking up lab order by ID: {}", labOrderId);
        LabOrder labOrder = labOrderRepository.findById(labOrderId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Lab order not found with id: " + labOrderId));
        logger.debug("Found lab order - ID: {}, Status: {}", labOrderId, labOrder.getStatus());
        return labOrder;
    }

    private Encounter findEncounter(Long encounterId) {
        logger.debug("Looking up encounter by ID: {}", encounterId);
        Encounter encounter = encounterRepository.findById(encounterId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Encounter not found with id: " + encounterId));
        logger.debug("Found encounter - ID: {}", encounterId);
        return encounter;
    }

    private Patient findPatient(Long patientId) {
        logger.debug("Looking up patient by ID: {}", patientId);
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Patient not found with id: " + patientId));
        logger.debug("Found patient - ID: {}, Name: {}", patientId, patient.getName());
        return patient;
    }

    private User findUser(Long userId) {
        logger.debug("Looking up user by ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found with id: " + userId));
        logger.debug("Found user - ID: {}, Username: {}", userId, user.getUsername());
        return user;
    }

    private List<LabOrderResponseDto> toOrderResponseList(List<LabOrder> labOrders) {
        List<LabOrderResponseDto> responseDtos = new ArrayList<>();

        for (LabOrder labOrder : labOrders) {
            responseDtos.add(toOrderResponseDto(labOrder));
        }

        return responseDtos;
    }

    private LabOrderResponseDto toOrderResponseDto(LabOrder labOrder) {
        User orderedBy = labOrder.getOrderedBy();
        List<LabResult> labResults = labResultRepository.findAllByLabOrderLabOrderId(labOrder.getLabOrderId());
        List<LabResultSummaryDto> results = new ArrayList<>();

        for (LabResult result : labResults) {
            results.add(new LabResultSummaryDto(
                    result.getResultId(),
                    result.getTestCode(),
                    result.getValue(),
                    result.getUnits(),
                    result.getFlag(),
                    result.getReportedAt()
            ));
        }

        return new LabOrderResponseDto(
                labOrder.getLabOrderId(),
                labOrder.getEncounter() != null ? labOrder.getEncounter().getEncounterId() : null,
                labOrder.getPatient().getPatientId(),
                labOrder.getPatient().getName(),
                orderedBy != null ? orderedBy.getUserId() : null,
                orderedBy != null ? orderedBy.getName() : null,
                labOrder.getTestsJson(),
                labOrder.getSampleId(),
                labOrder.getCollectedAt(),
                labOrder.getStatus(),
                labOrder.getResultUri(),
                results
        );
    }
}
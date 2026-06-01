package com.HospitalManagement.service;

import com.HospitalManagement.entity.LabOrder;
import com.HospitalManagement.entity.LabResult;
import com.HospitalManagement.entity.User;
import com.HospitalManagement.service.AuthService;
import com.HospitalManagement.enums.LabOrderStatus;
import com.HospitalManagement.enums.LabResultFlag;
import com.HospitalManagement.repository.LabOrderRepository;
import com.HospitalManagement.repository.LabResultRepository;
import com.HospitalManagement.repository.UserRepository;
import com.HospitalManagement.requestdto.LabResultRequestDto;
import com.HospitalManagement.responsedto.LabResultResponseDto;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class LabResultService {


    private static final Logger logger = LoggerFactory.getLogger(LabResultService.class);

    private final LabResultRepository labResultRepository;
    private final LabOrderRepository labOrderRepository;
    private final UserRepository userRepository;
    private final AuthService authService;


    public LabResultService(LabResultRepository labResultRepository,
                            LabOrderRepository labOrderRepository,
                            UserRepository userRepository,
                            AuthService authService) {

        this.labResultRepository = labResultRepository;
        this.labOrderRepository = labOrderRepository;
        this.userRepository = userRepository;
        this.authService = authService;
    }

    @Transactional(readOnly = true)
    public List<LabResultResponseDto> getAllResults() {
        logger.debug("Fetching all lab results");
        List<LabResult> labResults = labResultRepository.findAll();
        List<LabResultResponseDto> results = toResultResponseList(labResults);
        logger.info("Retrieved {} lab results", results.size());
        return results;
    }

    @Transactional(readOnly = true)
    public LabResultResponseDto getResultById(Long resultId) {
        logger.debug("Fetching lab result by ID: {}", resultId);
        LabResultResponseDto result = toResultResponseDto(findLabResult(resultId));
        logger.info("Retrieved lab result - ID: {}, Test: {}", resultId, result.testCode());
        return result;
    }

    @Transactional(readOnly = true)
    public List<LabResultResponseDto> getResultsByOrderId(Long labOrderId) {
        logger.debug("Fetching lab results for order ID: {}", labOrderId);
        findLabOrder(labOrderId);
        List<LabResult> labResults = labResultRepository.findAllByLabOrderLabOrderId(labOrderId);
        List<LabResultResponseDto> results = toResultResponseList(labResults);
        logger.info("Retrieved {} lab results for order ID: {}", results.size(), labOrderId);
        return results;
    }

    public LabResultResponseDto createResult(LabResultRequestDto requestDto) {
        logger.info("Creating lab result - Order ID: {}, Test: {}", requestDto.labOrderId(), requestDto.testCode());
        LabResult labResult = new LabResult();
        mapRequestToEntity(requestDto, labResult);
        LabResult savedResult = labResultRepository.save(labResult);
        updateLabOrderStatus(savedResult.getLabOrder());
        logger.info("Successfully created lab result - ID: {}", savedResult.getResultId());
        return toResultResponseDto(savedResult);
    }

    public LabResultResponseDto updateResult(Long resultId, LabResultRequestDto requestDto) {
        logger.info("Updating lab result - ID: {}", resultId);
        LabResult labResult = findLabResult(resultId);
        mapRequestToEntity(requestDto, labResult);
        LabResult savedResult = labResultRepository.save(labResult);
        updateLabOrderStatus(savedResult.getLabOrder());
        logger.info("Successfully updated lab result - ID: {}", resultId);
        return toResultResponseDto(savedResult);
    }

    public void deleteResult(Long resultId) {
        logger.info("Deleting lab result - ID: {}", resultId);
        LabResult labResult = findLabResult(resultId);
        LabOrder order = labResult.getLabOrder();
        labResultRepository.delete(labResult);
        updateLabOrderStatus(order);
        logger.info("Successfully deleted lab result - ID: {}", resultId);
    }

    private void mapRequestToEntity(LabResultRequestDto requestDto, LabResult labResult) {

        LabOrder labOrder = findLabOrder(requestDto.labOrderId());

        labResult.setLabOrder(labOrder);
        labResult.setTestCode(requestDto.testCode());
        labResult.setValue(requestDto.value());
        labResult.setUnits(requestDto.units());

        // Store reference range JSON directly (optional field)
        labResult.setReferenceRangeJson(
                requestDto.referenceRangeJson() != null && !requestDto.referenceRangeJson().isBlank()
                        ? requestDto.referenceRangeJson()
                        : null
        );

        // Use provided flag or default to NORMAL
        labResult.setFlag(
                requestDto.flag() != null
                        ? requestDto.flag()
                        : LabResultFlag.NORMAL
        );

        labResult.setReportedAt(
                requestDto.reportedAt() != null
                        ? requestDto.reportedAt()
                        : LocalDateTime.now()
        );

        labResult.setReportedBy(authService.getAuthenticatedUser());

        // Ensure resultUri exists on the order
        if (labOrder.getResultUri() == null || labOrder.getResultUri().isBlank()) {
            labOrder.setResultUri(buildResultUri(labOrder.getLabOrderId()));
        }
    }

    private void updateLabOrderStatus(LabOrder labOrder) {
        logger.debug("Updating lab order status - Order ID: {}", labOrder.getLabOrderId());

        List<LabResult> results =
                labResultRepository.findAllByLabOrderLabOrderId(labOrder.getLabOrderId());

        // No results yet
        if (results.isEmpty()) {

            if (labOrder.getCollectedAt() != null) {
                labOrder.setStatus(LabOrderStatus.COLLECTED);
            } else {
                labOrder.setStatus(LabOrderStatus.ORDERED);
            }

            labOrderRepository.save(labOrder);
            logger.debug("Set lab order status to {} (no results)", labOrder.getStatus());
            return;
        }
        // Check if any result is CRITICAL
        boolean hasCritical = false;

        for (LabResult result : results) {
            if (result.getFlag() == LabResultFlag.CRITICAL){
                hasCritical = true;
                break;
            }
        }
        // Update order workflow state
        labOrder.setStatus(
                hasCritical
                        ? LabOrderStatus.CRITICAL_REPORTED
                        : LabOrderStatus.RESULTS_REPORTED
        );
        // Ensure result URI exists
        if (labOrder.getResultUri() == null || labOrder.getResultUri().isBlank()) {
            labOrder.setResultUri(buildResultUri(labOrder.getLabOrderId()));
        }

        labOrderRepository.save(labOrder);
        logger.debug("Updated lab order status to {} - Critical: {}", labOrder.getStatus(), hasCritical);
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

    private LabResult findLabResult(Long resultId) {
        logger.debug("Looking up lab result by ID: {}", resultId);
        LabResult labResult = labResultRepository.findById(resultId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Lab result not found with id: " + resultId));
        logger.debug("Found lab result - ID: {}, Test: {}", resultId, labResult.getTestCode());
        return labResult;
    }

    private User findUser(Long userId) {
        logger.debug("Looking up user by ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found with id: " + userId));
        logger.debug("Found user - ID: {}, Username: {}", userId, user.getUsername());
        return user;
    }

    private List<LabResultResponseDto> toResultResponseList(List<LabResult> labResults) {
        List<LabResultResponseDto> responseDtos = new ArrayList<>();

        for (LabResult labResult : labResults) {
            responseDtos.add(toResultResponseDto(labResult));
        }

        return responseDtos;
    }

    private LabResultResponseDto toResultResponseDto(LabResult labResult) {
        User reportedBy = labResult.getReportedBy();
        return new LabResultResponseDto(
                labResult.getResultId(),
                labResult.getLabOrder().getLabOrderId(),
                labResult.getTestCode(),
                labResult.getValue(),
                labResult.getUnits(),
                labResult.getReferenceRangeJson(),
                labResult.getFlag(),
                labResult.getReportedAt(),
                reportedBy != null ? reportedBy.getUserId() : null,
                reportedBy != null ? reportedBy.getName() : null
        );
    }
}
package com.HospitalManagement.service;

import com.HospitalManagement.entity.InventoryItem;
import com.HospitalManagement.entity.MedicationMaster;
import com.HospitalManagement.enums.InventoryStatus;
import com.HospitalManagement.repository.InventoryItemRepository;
import com.HospitalManagement.repository.MedicationMasterRepository;
import com.HospitalManagement.requestdto.PharmacyRequestDto;
import com.HospitalManagement.responsedto.InventoryResponseDto;
import com.HospitalManagement.responsedto.StockSummaryResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class InventoryService {

    private static final Logger logger = LoggerFactory.getLogger(InventoryService.class);
    private static final int LOW_STOCK_LIMIT = 10;
    private final InventoryItemRepository inventoryRepository;
    private final MedicationMasterRepository medicationRepository;

    public InventoryService(InventoryItemRepository inventoryRepository, MedicationMasterRepository medicationRepository) {
        this.inventoryRepository = inventoryRepository;
        this.medicationRepository = medicationRepository;
    }

    @Transactional(readOnly = true)
    public List<InventoryResponseDto> getAllInventory() {
        logger.debug("Fetching all inventory items");
        List<InventoryResponseDto> inventory = toDtos(inventoryRepository.findAll());
        logger.info("Retrieved {} inventory items", inventory.size());
        return inventory;
    }

    @Transactional(readOnly = true)
    public InventoryResponseDto getInventoryItemById(Long inventoryId) {
        logger.debug("Fetching inventory item by ID: {}", inventoryId);
        InventoryResponseDto item = toDto(findInventoryItem(inventoryId));
        logger.info("Retrieved inventory item - ID: {}, Medication: {}", inventoryId, item.medicationName());
        return item;
    }

    @Transactional(readOnly = true)
    public List<InventoryResponseDto> getInventoryByMedication(Long medicationId) {
        logger.debug("Fetching inventory for medication ID: {}", medicationId);
        List<InventoryResponseDto> inventory = toDtos(inventoryRepository.findByMedicationMedId(medicationId));
        logger.info("Retrieved {} inventory items for medication ID: {}", inventory.size(), medicationId);
        return inventory;
    }

    @Transactional(readOnly = true)
    public List<InventoryResponseDto> getExpiringInventory(Integer days) {
        logger.debug("Fetching expiring inventory within {} days", days);
        LocalDate today = LocalDate.now();
        LocalDate until = today.plusDays(days == null ? 30 : days);
        List<InventoryResponseDto> expiring = toDtos(inventoryRepository.findByExpiryDateBetween(today, until));
        logger.info("Retrieved {} expiring inventory items within {} days", expiring.size(), days == null ? 30 : days);
        return expiring;
    }

    @Transactional(readOnly = true)
    public List<InventoryResponseDto> getExpiredInventory() {
        logger.debug("Fetching expired inventory");
        List<InventoryResponseDto> expired = toDtos(inventoryRepository.findByExpiryDateBefore(LocalDate.now()));
        logger.info("Retrieved {} expired inventory items", expired.size());
        return expired;
    }

    @Transactional(readOnly = true)
    public List<StockSummaryResponseDto> getStockSummary() {
        logger.debug("Generating stock summary");
        List<StockSummaryResponseDto> summary = new ArrayList<>();
        for (InventoryItem item : inventoryRepository.findAll()) {
            if (isDispensableBatch(item)) {
                addToSummary(summary, item);
            }
        }
        logger.info("Generated stock summary with {} items", summary.size());
        return summary;
    }

    @Transactional(readOnly = true)
    public List<StockSummaryResponseDto> getLowStock(Integer threshold) {
        logger.debug("Fetching low stock items with threshold: {}", threshold);
        int limit = threshold == null ? LOW_STOCK_LIMIT : threshold;
        List<StockSummaryResponseDto> response = new ArrayList<>();
        for (StockSummaryResponseDto item : getStockSummary()) {
            if (item.totalQuantity() <= limit) {
                response.add(item);
            }
        }
        logger.info("Retrieved {} low stock items (threshold: {})", response.size(), limit);
        return response;
    }

    public InventoryResponseDto createInventoryItem(PharmacyRequestDto request) {
        logger.info("Creating inventory item - Medication ID: {}, Batch: {}", request.medicationId(), request.batchNumber());
        MedicationMaster medication = findMedication(request.medicationId());
        if (inventoryRepository.findByMedicationAndBatchNumberIgnoreCase(medication, request.batchNumber()).isPresent()) {
            logger.warn("Attempted to create duplicate batch - Medication ID: {}, Batch: {}", request.medicationId(), request.batchNumber());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Batch already exists");
        }
        InventoryItem item = new InventoryItem();
        copyRequest(request, item, medication);
        InventoryItem savedItem = inventoryRepository.save(item);
        logger.info("Successfully created inventory item - ID: {}, Batch: {}", savedItem.getInventoryId(), request.batchNumber());
        return toDto(savedItem);
    }

    public InventoryResponseDto updateInventoryItem(Long inventoryId, PharmacyRequestDto request) {
        logger.info("Updating inventory item - ID: {}", inventoryId);
        InventoryItem item = findInventoryItem(inventoryId);
        MedicationMaster medication = findMedication(request.medicationId());
        copyRequest(request, item, medication);
        InventoryItem updatedItem = inventoryRepository.save(item);
        logger.info("Successfully updated inventory item - ID: {}", inventoryId);
        return toDto(updatedItem);
    }

    public InventoryResponseDto adjustStock(Long inventoryId, PharmacyRequestDto request) {
        logger.info("Adjusting stock - Inventory ID: {}, Delta: {}", inventoryId, request.quantityDelta());
        InventoryItem item = findInventoryItem(inventoryId);
        int updatedQuantity = item.getQuantity() + request.quantityDelta();
        if (updatedQuantity < 0) {
            logger.warn("Stock adjustment would result in negative quantity - Current: {}, Delta: {}", item.getQuantity(), request.quantityDelta());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Stock cannot become negative");
        }
        item.setQuantity(updatedQuantity);
        if (updatedQuantity == 0) {
            item.setStatus(InventoryStatus.OUT_OF_STOCK);
        }
        else {
            item.setStatus(InventoryStatus.IN_STOCK);
        }
        InventoryItem adjustedItem = inventoryRepository.save(item);
        logger.info("Successfully adjusted stock - Inventory ID: {}, New Quantity: {}", inventoryId, updatedQuantity);
        return toDto(adjustedItem);
    }

    public void deleteInventoryItem(Long inventoryId) {
        logger.info("Deleting inventory item - ID: {}", inventoryId);
        inventoryRepository.delete(findInventoryItem(inventoryId));
        logger.info("Successfully deleted inventory item - ID: {}", inventoryId);
    }

    InventoryItem findInventoryItem(Long inventoryId) {
        logger.debug("Looking up inventory item by ID: {}", inventoryId);
        InventoryItem item = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inventory item not found"));
        logger.debug("Found inventory item - ID: {}, Batch: {}", inventoryId, item.getBatchNumber());
        return item;
    }

    boolean isDispensableBatch(InventoryItem item) {
        return item.getQuantity() != null
                && item.getQuantity() > 0
                && item.getExpiryDate() != null
                && !item.getExpiryDate().isBefore(LocalDate.now())
                && item.getStatus() == InventoryStatus.IN_STOCK;
    }





    private MedicationMaster findMedication(Long medicationId) {
        logger.debug("Looking up medication by ID: {}", medicationId);
        MedicationMaster medication = medicationRepository.findById(medicationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Medication not found"));
        logger.debug("Found medication - ID: {}, Name: {}", medicationId, medication.getName());
        return medication;
    }

    private void copyRequest(PharmacyRequestDto request, InventoryItem item, MedicationMaster medication) {
        item.setMedication(medication);
        item.setBatchNumber(request.batchNumber());
        item.setQuantity(request.quantity());
        item.setUnit(request.unit());
        item.setExpiryDate(request.expiryDate());
        item.setLocation(request.location());
        item.setCostPrice(request.costPrice());

        // derive status
        updateInventoryStatus(item);
    }

    private void updateInventoryStatus(InventoryItem item) {
        logger.debug("Updating inventory status for item ID: {}", item.getInventoryId());
        if (item.getExpiryDate().isBefore(LocalDate.now())) {
            item.setStatus(InventoryStatus.EXPIRED);
            logger.debug("Set status to EXPIRED for item ID: {}", item.getInventoryId());
        } else if (item.getQuantity() == 0) {
            item.setStatus(InventoryStatus.OUT_OF_STOCK);
            logger.debug("Set status to OUT_OF_STOCK for item ID: {}", item.getInventoryId());
        } else if (item.getQuantity() <= LOW_STOCK_LIMIT) {
            item.setStatus(InventoryStatus.LOW_STOCK);
            logger.debug("Set status to LOW_STOCK for item ID: {}", item.getInventoryId());
        } else {
            item.setStatus(InventoryStatus.IN_STOCK);
            logger.debug("Set status to IN_STOCK for item ID: {}", item.getInventoryId());
        }
    }


    private void addToSummary(List<StockSummaryResponseDto> summary, InventoryItem item) {
        for (int i = 0; i < summary.size(); i++) {
            StockSummaryResponseDto old = summary.get(i);
            if (old.medicationId().equals(item.getMedication().getMedId())) {
                summary.set(i, new StockSummaryResponseDto(old.medicationId(), old.medicationName(),
                        old.totalQuantity() + item.getQuantity(), old.unit(), old.batchCount() + 1));
                return;
            }
        }
        summary.add(new StockSummaryResponseDto(item.getMedication().getMedId(),
                item.getMedication().getName(), item.getQuantity(), item.getUnit(), 1));
    }

    private List<InventoryResponseDto> toDtos(List<InventoryItem> items) {
        List<InventoryResponseDto> response = new ArrayList<>();
        for (InventoryItem item : items) {
            response.add(toDto(item));
        }
        return response;
    }

    private InventoryResponseDto toDto(InventoryItem item) {
        LocalDate expiryDate = item.getExpiryDate();
        return new InventoryResponseDto(
                item.getInventoryId(),
                item.getMedication().getMedId(),
                item.getMedication().getCode(),
                item.getMedication().getName(),
                item.getBatchNumber(),
                item.getQuantity(),
                item.getUnit(),
                expiryDate,
                item.getLocation(),
                item.getCostPrice(),
                item.getStatus(),
                expiryDate != null && expiryDate.isBefore(LocalDate.now())
        );
    }
}
package com.HospitalManagement.repository;

import com.HospitalManagement.entity.InventoryItem;
import com.HospitalManagement.entity.MedicationMaster;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {
    List<InventoryItem> findByMedicationMedId(Long medId);
    List<InventoryItem> findByStatusIgnoreCase(String status);
    List<InventoryItem> findByExpiryDateBefore(LocalDate date);
    List<InventoryItem> findByExpiryDateBetween(LocalDate from, LocalDate to);
    Optional<InventoryItem> findByMedicationAndBatchNumberIgnoreCase(MedicationMaster medication, String batchNumber);
}
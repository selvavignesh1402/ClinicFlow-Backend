package com.HospitalManagement.repository;

import com.HospitalManagement.entity.MedicationMaster;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MedicationMasterRepository extends JpaRepository<MedicationMaster, Long> {
    Optional<MedicationMaster> findByCode(String code);
    boolean existsByCode(String code);
    List<MedicationMaster> findByStatusIgnoreCase(String status);
    List<MedicationMaster> findByNameContainingIgnoreCaseOrCodeContainingIgnoreCase(String name, String code);
}
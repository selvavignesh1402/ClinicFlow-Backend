package com.HospitalManagement.repository;

import com.HospitalManagement.entity.MedicationMaster;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicationMasterRepository extends JpaRepository<MedicationMaster, Long> {
}

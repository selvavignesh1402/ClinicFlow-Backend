package com.HospitalManagement.repository;

import com.HospitalManagement.entity.DispenseRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DispenseRecordRepository extends JpaRepository<DispenseRecord, Long> {
    List<DispenseRecord> findByPrescriptionRxId(Long rxId);
    List<DispenseRecord> findByPatientPatientId(Long patientId);
    List<DispenseRecord> findByDispensedByUserId(Long userId);
}
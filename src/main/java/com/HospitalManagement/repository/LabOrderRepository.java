package com.HospitalManagement.repository;

import com.HospitalManagement.entity.LabOrder;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LabOrderRepository extends JpaRepository<LabOrder, Long> {
    List<LabOrder> findAllByPatientPatientId(Long patientId);
}
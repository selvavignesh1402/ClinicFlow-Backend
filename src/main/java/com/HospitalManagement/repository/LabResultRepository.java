package com.HospitalManagement.repository;

import com.HospitalManagement.entity.LabResult;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LabResultRepository extends JpaRepository<LabResult, Long> {
    List<LabResult> findAllByLabOrderLabOrderId(Long labOrderId);
}
package com.HospitalManagement.repository;

import com.HospitalManagement.entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
    List<Prescription> findByEncounterEncounterId(Long encounterId);
}
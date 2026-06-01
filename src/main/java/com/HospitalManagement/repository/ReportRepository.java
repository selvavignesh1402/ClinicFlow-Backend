package com.HospitalManagement.repository;

import com.HospitalManagement.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
}
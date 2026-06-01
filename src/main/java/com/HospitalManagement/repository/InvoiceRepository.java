package com.HospitalManagement.repository;

import com.HospitalManagement.entity.Invoice;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    List<Invoice> findByPatientPatientId(Long patientId);
}
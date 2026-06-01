package com.HospitalManagement.repository;

import com.HospitalManagement.entity.Payment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByInvoiceInvoiceId(Long invoiceId);
    Payment findTopByInvoiceInvoiceIdOrderByPaidAtDescPaymentIdDesc(Long invoiceId);
}
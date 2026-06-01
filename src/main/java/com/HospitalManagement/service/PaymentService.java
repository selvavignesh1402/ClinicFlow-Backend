package com.HospitalManagement.service;

import com.HospitalManagement.entity.Invoice;
import com.HospitalManagement.entity.Patient;
import com.HospitalManagement.entity.Payment;
import com.HospitalManagement.enums.InvoiceStatus;
import com.HospitalManagement.exception.BadRequestException;
import com.HospitalManagement.exception.ResourceNotFoundException;
import com.HospitalManagement.repository.PatientRepository;
import com.HospitalManagement.repository.PaymentRepository;
import com.HospitalManagement.requestdto.PaymentRequestDto;
import com.HospitalManagement.responsedto.PaymentResponseDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentRepository paymentRepository;
    private final PatientRepository patientRepository;
    private final InvoiceService invoiceService;

    public PaymentResponseDto recordManualPayment(PaymentRequestDto requestDto) {

        logger.info(
                "Recording manual payment | InvoiceId: {}, PatientId: {}, Amount: {}",
                requestDto.invoiceId(),
                requestDto.patientId(),
                requestDto.amount()
        );

        Invoice invoice = invoiceService.getInvoiceEntity(requestDto.invoiceId());
        Patient patient = findPatient(requestDto.patientId());

        if (!invoice.getPatient().getPatientId().equals(patient.getPatientId())) {
            logger.warn(
                    "Patient mismatch | InvoicePatientId: {}, PaymentPatientId: {}",
                    invoice.getPatient().getPatientId(),
                    patient.getPatientId()
            );
            throw new BadRequestException("Payment patient does not match invoice patient");
        }

        if (InvoiceStatus.PAID.equals(invoice.getStatus())) {
            logger.warn("Attempted payment on already PAID invoice | InvoiceId: {}", invoice.getInvoiceId());
            throw new BadRequestException("Invoice is already paid");
        }

        if (requestDto.amount() == null || requestDto.amount() <= 0) {
            logger.warn("Invalid payment amount: {}", requestDto.amount());
            throw new BadRequestException("Payment amount must be greater than zero");
        }

        Payment payment = new Payment();
        payment.setInvoice(invoice);
        payment.setPatient(patient);
        payment.setAmount(requestDto.amount());
        payment.setMethod(requestDto.method());
        payment.setPaidAt(LocalDateTime.now());
        payment.setStatus("COMPLETED");

        Payment savedPayment = paymentRepository.save(payment);

        updateInvoiceStatus(invoice);

        logger.info(
                "Payment recorded successfully | PaymentId: {}, InvoiceId: {}",
                savedPayment.getPaymentId(),
                invoice.getInvoiceId()
        );

        return toResponse(savedPayment);
    }

    @Transactional(readOnly = true)
    public List<PaymentResponseDto> getAllPayments() {

        logger.debug("Fetching all payments");

        List<Payment> payments = paymentRepository.findAll();
        List<PaymentResponseDto> response = new ArrayList<>();

        for (Payment payment : payments) {
            response.add(toResponse(payment));
        }

        logger.info("Total payments retrieved: {}", response.size());
        return response;
    }

    @Transactional(readOnly = true)
    public List<PaymentResponseDto> getInvoicePayments(Long invoiceId) {

        logger.debug("Fetching payments for InvoiceId: {}", invoiceId);

        List<Payment> payments = paymentRepository.findByInvoiceInvoiceId(invoiceId);
        List<PaymentResponseDto> response = new ArrayList<>();

        for (Payment payment : payments) {
            response.add(toResponse(payment));
        }

        logger.info(
                "Payments retrieved for InvoiceId {}: {}",
                invoiceId,
                response.size()
        );

        return response;
    }

    @Transactional(readOnly = true)
    public PaymentResponseDto getPaymentById(Long paymentId) {

        logger.debug("Fetching payment by Id: {}", paymentId);

        Payment payment = findPayment(paymentId);

        logger.info(
                "Payment found | PaymentId: {}, Amount: {}",
                payment.getPaymentId(),
                payment.getAmount()
        );

        return toResponse(payment);
    }

    public void deletePayment(Long paymentId) {

        logger.info("Deleting payment | PaymentId: {}", paymentId);

        Payment payment = findPayment(paymentId);
        Invoice invoice = payment.getInvoice();

        paymentRepository.delete(payment);
        updateInvoiceStatus(invoice);

        logger.info("Payment deleted successfully | PaymentId: {}", paymentId);
    }

    private void updateInvoiceStatus(Invoice invoice) {

        logger.debug("Updating invoice status | InvoiceId: {}", invoice.getInvoiceId());

        List<Payment> payments = paymentRepository.findByInvoiceInvoiceId(invoice.getInvoiceId());

        double totalPaid = 0.0;
        for (Payment p : payments) {
            if (p.getAmount() != null) {
                totalPaid += p.getAmount();
            }
        }

        String status;
        if (totalPaid <= 0) {
            status = "UNPAID";
        } else if (totalPaid >= invoice.getTotalAmount()) {
            status = "PAID";
        } else {
            status = "PARTIALLY_PAID";
        }

        invoiceService.updateInvoiceStatus(invoice.getInvoiceId(), status);

        logger.debug(
                "Invoice status updated | InvoiceId: {}, Status: {}",
                invoice.getInvoiceId(),
                status
        );
    }

    private Payment findPayment(Long paymentId) {

        logger.debug("Looking up payment | PaymentId: {}", paymentId);

        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> {
                    logger.error("Payment not found | PaymentId: {}", paymentId);
                    return new ResourceNotFoundException("Payment not found with id: " + paymentId);
                });
    }

    private Patient findPatient(Long patientId) {

        logger.debug("Looking up patient | PatientId: {}", patientId);

        return patientRepository.findById(patientId)
                .orElseThrow(() -> {
                    logger.error("Patient not found | PatientId: {}", patientId);
                    return new ResourceNotFoundException("Patient not found with id: " + patientId);
                });
    }

    private PaymentResponseDto toResponse(Payment payment) {

        String patientName = null;
        if (payment.getPatient() != null) {
            patientName = payment.getPatient().getName();
        }

        return new PaymentResponseDto(
                payment.getPaymentId(),
                payment.getInvoice().getInvoiceId(),
                payment.getPatient().getPatientId(),
                patientName,
                payment.getAmount(),
                payment.getMethod(),
                payment.getPaidAt(),
                payment.getStatus()
        );
    }
}
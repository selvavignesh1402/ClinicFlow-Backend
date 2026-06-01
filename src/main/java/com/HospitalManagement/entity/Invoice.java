package com.HospitalManagement.entity;

import com.HospitalManagement.enums.InvoiceStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "invoice")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long invoiceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "encounter_id")
    private Encounter encounter; // Can be null

    @Column(columnDefinition = "json")
    private String lineItemsJson;

    private Double subtotal;
    private Double taxes;
    private Double discounts;
    private Double totalAmount;

    private LocalDateTime issuedAt;
    private LocalDateTime dueDate;
    private InvoiceStatus status;
}
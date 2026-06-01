package com.HospitalManagement.entity;

import com.HospitalManagement.enums.DispenseStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "dispense_record")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DispenseRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dispenseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rx_id", nullable = false)
    private Prescription prescription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_id")
    private InventoryItem inventoryItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dispensed_by_fk")
    private User dispensedBy;

    private Integer quantity;
    private LocalDateTime dispensedAt;

    private String notes;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DispenseStatus status;
}
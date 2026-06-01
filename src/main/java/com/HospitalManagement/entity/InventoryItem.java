package com.HospitalManagement.entity;

import com.HospitalManagement.enums.InventoryStatus;
import com.HospitalManagement.enums.InvoiceStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "inventory_item")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inventoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "med_id", nullable = false)
    private MedicationMaster medication;

    private String batchNumber;
    private Integer quantity;
    private String unit;
    private LocalDate expiryDate;
    private String location;
    private Double costPrice;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InventoryStatus status;
}
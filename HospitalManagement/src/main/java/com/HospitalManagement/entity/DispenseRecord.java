// package com.HospitalManagement.entity;

// import jakarta.persistence.Column;
// import jakarta.persistence.Entity;
// import jakarta.persistence.FetchType;
// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.GenerationType;
// import jakarta.persistence.Id;
// import jakarta.persistence.JoinColumn;
// import jakarta.persistence.ManyToOne;
// import jakarta.persistence.Table;
// import java.time.LocalDateTime;
// import lombok.AllArgsConstructor;
// import lombok.Getter;
// import lombok.NoArgsConstructor;
// import lombok.Setter;

// @Entity
// @Table(name = "dispense_records")
// @Getter
// @Setter
// @NoArgsConstructor
// @AllArgsConstructor
// public class DispenseRecord {

//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long dispenseId;

//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "prescription_id", nullable = false)
//     private Prescription prescription;

//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "inventory_item_id", nullable = false)
//     private InventoryItem inventoryItem;

//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "patient_id", nullable = false)
//     private Patient patient;

//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "dispensed_by", nullable = false)
//     private User dispensedBy;

//     @Column(nullable = false)
//     private Integer quantity;

//     @Column(nullable = false)
//     private LocalDateTime dispensedAt;

//     @Column(columnDefinition = "TEXT")
//     private String notes;

//     @Column(nullable = false)
//     private String status;
// }


package com.HospitalManagement.entity;

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
    private String status;
}
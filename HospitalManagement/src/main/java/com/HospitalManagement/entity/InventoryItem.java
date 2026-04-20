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
// import java.math.BigDecimal;
// import java.time.LocalDate;
// import lombok.AllArgsConstructor;
// import lombok.Getter;
// import lombok.NoArgsConstructor;
// import lombok.Setter;

// @Entity
// @Table(name = "inventory_items")
// @Getter
// @Setter
// @NoArgsConstructor
// @AllArgsConstructor
// public class InventoryItem {

//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long inventoryId;

//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "medication_id", nullable = false)
//     private MedicationMaster medication;

//     @Column(nullable = false)
//     private String batchNumber;

//     @Column(nullable = false)
//     private Integer quantity;

//     @Column(nullable = false)
//     private String unit;

//     @Column(nullable = false)
//     private LocalDate expiryDate;

//     @Column(nullable = false)
//     private String location;

//     @Column(nullable = false, precision = 15, scale = 2)
//     private BigDecimal costPrice;

//     @Column(nullable = false)
//     private String status;
// }


package com.HospitalManagement.entity;

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
    private String status;
}
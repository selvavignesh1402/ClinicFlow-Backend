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
// import java.time.LocalDateTime;
// import lombok.AllArgsConstructor;
// import lombok.Getter;
// import lombok.NoArgsConstructor;
// import lombok.Setter;

// @Entity
// @Table(name = "invoices")
// @Getter
// @Setter
// @NoArgsConstructor
// @AllArgsConstructor
// public class Invoice {

//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long invoiceId;

//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "patient_id", nullable = false)
//     private Patient patient;

//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "encounter_id")
//     private Encounter encounter;

//     @Column(nullable = false, columnDefinition = "JSON")
//     private String lineItemsJson;

//     @Column(nullable = false, precision = 15, scale = 2)
//     private BigDecimal subtotal;

//     @Column(nullable = false, precision = 15, scale = 2)
//     private BigDecimal taxes;

//     @Column(nullable = false, precision = 15, scale = 2)
//     private BigDecimal discounts;

//     @Column(nullable = false, precision = 15, scale = 2)
//     private BigDecimal totalAmount;

//     @Column(nullable = false)
//     private LocalDateTime issuedAt;

//     @Column(nullable = false)
//     private LocalDate dueDate;

//     @Column(nullable = false)
//     private String status;
// }


package com.HospitalManagement.entity;

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
    private String status;
}
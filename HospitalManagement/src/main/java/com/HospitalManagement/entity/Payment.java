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
// import java.time.LocalDateTime;
// import lombok.AllArgsConstructor;
// import lombok.Getter;
// import lombok.NoArgsConstructor;
// import lombok.Setter;

// @Entity
// @Table(name = "payments")
// @Getter
// @Setter
// @NoArgsConstructor
// @AllArgsConstructor
// public class Payment {

//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long paymentId;

//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "invoice_id", nullable = false)
//     private Invoice invoice;

//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "patient_id", nullable = false)
//     private Patient patient;

//     @Column(nullable = false, precision = 15, scale = 2)
//     private BigDecimal amount;

//     @Column(nullable = false)
//     private String method;

//     @Column(nullable = false)
//     private LocalDateTime paidAt;

//     @Column(nullable = false)
//     private String status;
// }


package com.HospitalManagement.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;
    
    private Double amount;
    private String method;
    private LocalDateTime paidAt;
    private String status;
}
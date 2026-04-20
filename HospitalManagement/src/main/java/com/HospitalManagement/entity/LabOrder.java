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
// @Table(name = "lab_orders")
// @Getter
// @Setter
// @NoArgsConstructor
// @AllArgsConstructor
// public class LabOrder {

//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long labOrderId;

//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "encounter_id", nullable = false)
//     private Encounter encounter;

//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "patient_id", nullable = false)
//     private Patient patient;

//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "ordered_by", nullable = false)
//     private User orderedBy;

//     @Column(nullable = false, columnDefinition = "JSON")
//     private String testsJson;

//     @Column(nullable = false)
//     private String sampleId;

//     private LocalDateTime collectedAt;

//     @Column(nullable = false)
//     private String status;

//     private String resultUri;
// }


package com.HospitalManagement.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "lab_order")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LabOrder {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long labOrderId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "encounter_id")
    private Encounter encounter;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ordered_by_fk")
    private User orderedBy;
    
    @Column(columnDefinition = "json")
    private String testsJson;
    
    private String sampleId;
    private LocalDateTime collectedAt;
    private String status;
    private String resultUri;
}
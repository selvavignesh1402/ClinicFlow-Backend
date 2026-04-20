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
// @Table(name = "prescriptions")
// @Getter
// @Setter
// @NoArgsConstructor
// @AllArgsConstructor
// public class Prescription {

//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long rxId;

//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "encounter_id", nullable = false)
//     private Encounter encounter;

//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "patient_id", nullable = false)
//     private Patient patient;

//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "clinician_id", nullable = false)
//     private User clinician;

//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "medication_id", nullable = false)
//     private MedicationMaster medication;

//     @Column(nullable = false)
//     private String dosage;

//     @Column(nullable = false)
//     private String frequency;

//     @Column(nullable = false)
//     private Integer durationDays;

//     @Column(nullable = false)
//     private Integer quantity;

//     @Column(nullable = false)
//     private Integer repeats;

//     @Column(nullable = false)
//     private String route;

//     @Column(columnDefinition = "TEXT")
//     private String notes;

//     @Column(nullable = false)
//     private String status;

//     private LocalDateTime issuedAt;
// }


package com.HospitalManagement.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "prescriptions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rxId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "encounter_id", nullable = false)
    private Encounter encounter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clinician_id", nullable = false)
    private User clinician;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "med_id", nullable = false)
    private MedicationMaster medication;

    private String dosage;

    private String frequency;

    private Integer durationDays;

    private Integer quantity;

    private Integer repeats;

    private String route;
    
    private String notes;

    private String status;

    private LocalDateTime issuedAt;
}
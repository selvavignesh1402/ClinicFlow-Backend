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
// @Table(name = "appointments")
// @Getter
// @Setter
// @NoArgsConstructor
// @AllArgsConstructor
// public class Appointment {

//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long apptId;

//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "patient_id", nullable = false)
//     private Patient patient;

//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "clinician_id", nullable = false)
//     private User clinician;

//     @Column(nullable = false)
//     private String department;

//     @Column(nullable = false)
//     private String serviceType;

//     @Column(nullable = false)
//     private LocalDateTime startAt;

//     @Column(nullable = false)
//     private LocalDateTime endAt;

//     @Column(nullable = false)
//     private String status;

//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "created_by", nullable = false)
//     private User createdBy;

//     @Column(nullable = false)
//     private LocalDateTime createdAt;
// }

package com.HospitalManagement.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long apptId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clinician_id", nullable = false)
    private User clinician;

    private String department;

    private String serviceType;

    private LocalDateTime startAt;
    private LocalDateTime endAt;

    private String status; // Scheduled, CheckedIn, Completed, Cancelled, NoShow

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
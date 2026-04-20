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
// import java.time.LocalDate;
// import java.time.LocalDateTime;
// import lombok.AllArgsConstructor;
// import lombok.Getter;
// import lombok.NoArgsConstructor;
// import lombok.Setter;

// @Entity
// @Table(name = "problem_list")
// @Getter
// @Setter
// @NoArgsConstructor
// @AllArgsConstructor
// public class ProblemList {

//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long problemId;

//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "patient_id", nullable = false)
//     private Patient patient;

//     @Column(nullable = false)
//     private String code;

//     @Column(nullable = false)
//     private String description;

//     private LocalDate onsetDate;

//     @Column(nullable = false)
//     private String status;

//     @Column(nullable = false)
//     private LocalDateTime createdAt;
// }


package com.HospitalManagement.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "problem_list")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProblemList {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long problemId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;
    
    private String code;
    private String description;
    private LocalDate onsetDate;
    private String status;
    
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
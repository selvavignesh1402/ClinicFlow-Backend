// package com.HospitalManagement.entity;

// import jakarta.persistence.Column;
// import jakarta.persistence.Entity;
// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.GenerationType;
// import jakarta.persistence.Id;
// import jakarta.persistence.Table;
// import java.time.LocalDate;
// import java.time.LocalDateTime;
// import lombok.AllArgsConstructor;
// import lombok.Getter;
// import lombok.NoArgsConstructor;
// import lombok.Setter;

// @Entity
// @Table(name = "patients")
// @Getter
// @Setter
// @NoArgsConstructor
// @AllArgsConstructor
// public class Patient {

//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long patientId;

//     @Column(nullable = false, unique = true)
//     private String mrn;

//     @Column(nullable = false)
//     private String name;

//     @Column(nullable = false)
//     private LocalDate dob;

//     @Column(nullable = false)
//     private String gender;

//     @Column(nullable = false, columnDefinition = "JSON")
//     private String contactInfoJson;

//     @Column(nullable = false, columnDefinition = "JSON")
//     private String addressJson;

//     @Column(nullable = false)
//     private String primaryContact;

//     private String insuranceId;

//     @Column(nullable = false)
//     private String status;

//     @Column(nullable = false)
//     private LocalDateTime createdAt;
// }


package com.HospitalManagement.entity;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "patients")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long patientId;

    @Column(unique = true, nullable = false)
    private String mrn;

    @Column(nullable = false)
    private String name;

    private LocalDate dob;
    private String gender;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private String contactInfoJson;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private String addressJson;

    private String primaryContact;

    private String insuranceId;

    private String status;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

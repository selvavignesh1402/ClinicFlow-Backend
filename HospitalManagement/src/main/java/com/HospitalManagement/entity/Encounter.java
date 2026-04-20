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
// @Table(name = "encounters")
// @Getter
// @Setter
// @NoArgsConstructor
// @AllArgsConstructor
// public class Encounter {

//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long encounterId;

//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "patient_id", nullable = false)
//     private Patient patient;

//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "clinician_id", nullable = false)
//     private User clinician;

//     @Column(nullable = false)
//     private String visitType;

//     @Column(nullable = false)
//     private String chiefComplaint;

//     @Column(columnDefinition = "JSON")
//     private String vitalsJson;

//     @Column(columnDefinition = "JSON")
//     private String notesJson;

//     @Column(columnDefinition = "JSON")
//     private String diagnosesJson;

//     @Column(columnDefinition = "JSON")
//     private String ordersJson;

//     @Column(columnDefinition = "JSON")
//     private String prescriptionsJson;

//     @Column(nullable = false)
//     private LocalDateTime startAt;

//     private LocalDateTime endAt;

//     @Column(nullable = false)
//     private String status;

//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "signed_by")
//     private User signedBy;

//     private LocalDateTime signedAt;
// }

package com.HospitalManagement.entity;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "encounters")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Encounter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long encounterId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clinician_id", nullable = false)
    private User clinician;

    private String visitType;
    private String chiefComplaint;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private String vitalsJson;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private String notesJson;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private String diagnosesJson;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private String ordersJson;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private String prescriptionsJson;

    private LocalDateTime startAt;
    private LocalDateTime endAt;

    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "signed_by")
    private User signedBy;

    private LocalDateTime signedAt;
}
// package com.HospitalManagement.entity;

// import jakarta.persistence.Column;
// import jakarta.persistence.Entity;
// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.GenerationType;
// import jakarta.persistence.Id;
// import jakarta.persistence.Table;
// import lombok.AllArgsConstructor;
// import lombok.Getter;
// import lombok.NoArgsConstructor;
// import lombok.Setter;

// @Entity
// @Table(name = "medication_master")
// @Getter
// @Setter
// @NoArgsConstructor
// @AllArgsConstructor
// public class MedicationMaster {

//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long medId;

//     @Column(nullable = false)
//     private String code;

//     @Column(nullable = false)
//     private String name;

//     @Column(nullable = false)
//     private String formulation;

//     @Column(nullable = false)
//     private String strength;

//     @Column(nullable = false)
//     private String atcCode;

//     @Column(nullable = false)
//     private Boolean controlledFlag;

//     @Column(nullable = false)
//     private String status;
// }


package com.HospitalManagement.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "medication_master")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicationMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long medId;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    private String formulation;
    private String strength;
    private String atcCode;

    private Boolean controlledFlag;
    private String status;
}
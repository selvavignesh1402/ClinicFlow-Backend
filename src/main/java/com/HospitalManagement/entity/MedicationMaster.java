package com.HospitalManagement.entity;

import com.HospitalManagement.enums.MedicationStatus;
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MedicationStatus status;
}
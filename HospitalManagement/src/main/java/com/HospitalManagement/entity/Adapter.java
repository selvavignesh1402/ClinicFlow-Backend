package com.HospitalManagement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "adapters")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Adapter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long adapterId;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false, columnDefinition = "JSON")
    private String configJson;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String credentialsEncrypted;

    private LocalDateTime lastSyncAt;

    @Column(nullable = false)
    private String status;
}

// package com.HospitalManagement.entity;

// import jakarta.persistence.Column;
// import jakarta.persistence.Entity;
// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.GenerationType;
// import jakarta.persistence.Id;
// import jakarta.persistence.Table;
// import java.math.BigDecimal;
// import lombok.AllArgsConstructor;
// import lombok.Getter;
// import lombok.NoArgsConstructor;
// import lombok.Setter;

// @Entity
// @Table(name = "kpis")
// @Getter
// @Setter
// @NoArgsConstructor
// @AllArgsConstructor
// public class KPI {

//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long kpiId;

//     @Column(nullable = false)
//     private String name;

//     @Column(nullable = false, columnDefinition = "TEXT")
//     private String definition;

//     @Column(nullable = false, precision = 15, scale = 2)
//     private BigDecimal target;

//     @Column(nullable = false, precision = 15, scale = 2)
//     private BigDecimal currentValue;

//     @Column(nullable = false)
//     private String reportingPeriod;
// }


package com.HospitalManagement.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "kpi")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KPI {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long kpiId;
    
    private String name;
    private String definition;
    private Double target;
    private Double currentValue;
    private String reportingPeriod;
}
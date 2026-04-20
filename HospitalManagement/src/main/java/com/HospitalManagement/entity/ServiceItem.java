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
// @Table(name = "service_items")
// @Getter
// @Setter
// @NoArgsConstructor
// @AllArgsConstructor
// public class ServiceItem {

//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long serviceId;

//     @Column(nullable = false)
//     private String code;

//     @Column(nullable = false)
//     private String description;

//     @Column(nullable = false, precision = 15, scale = 2)
//     private BigDecimal price;

//     @Column(nullable = false)
//     private String department;

//     @Column(nullable = false)
//     private String billingCategory;

//     @Column(nullable = false)
//     private String status;
// }


package com.HospitalManagement.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "service_item")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long serviceId;
    
    private String code;
    private String description;
    private Double price;
    private String department;
    private String billingCategory;
    private String status;
}
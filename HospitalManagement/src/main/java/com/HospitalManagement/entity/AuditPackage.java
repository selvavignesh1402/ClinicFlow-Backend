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
// @Table(name = "audit_packages")
// @Getter
// @Setter
// @NoArgsConstructor
// @AllArgsConstructor
// public class AuditPackage {

//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long packageId;

//     @Column(nullable = false)
//     private LocalDate periodStart;

//     @Column(nullable = false)
//     private LocalDate periodEnd;

//     @Column(nullable = false, columnDefinition = "JSON")
//     private String contentsJson;

//     @Column(nullable = false)
//     private LocalDateTime generatedAt;

//     @Column(nullable = false)
//     private String packageUri;
// }


package com.HospitalManagement.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_package")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditPackage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long packageId;
    
    private LocalDateTime periodStart;
    private LocalDateTime periodEnd;
    
    @Column(columnDefinition = "json")
    private String contentsJson;
    
    private LocalDateTime generatedAt;
    private String packageUri;
}
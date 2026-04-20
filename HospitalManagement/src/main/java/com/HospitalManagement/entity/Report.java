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
// @Table(name = "reports")
// @Getter
// @Setter
// @NoArgsConstructor
// @AllArgsConstructor
// public class Report {

//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long reportId;

//     @Column(nullable = false)
//     private String scope;

//     @Column(nullable = false, columnDefinition = "JSON")
//     private String parametersJson;

//     @Column(nullable = false, columnDefinition = "JSON")
//     private String metricsJson;

//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "generated_by", nullable = false)
//     private User generatedBy;

//     @Column(nullable = false)
//     private LocalDateTime generatedAt;

//     @Column(nullable = false)
//     private String reportUri;
// }


package com.HospitalManagement.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "report")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Report {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId;
    
    private String scope; 
    
    @Column(columnDefinition = "json")
    private String parametersJson;
    
    @Column(columnDefinition = "json")
    private String metricsJson;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "generated_by_fk")
    private User generatedBy;
    
    private LocalDateTime generatedAt;
    private String reportUri;
}
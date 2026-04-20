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
// @Table(name = "lab_results")
// @Getter
// @Setter
// @NoArgsConstructor
// @AllArgsConstructor
// public class LabResult {

//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long resultId;

//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "lab_order_id", nullable = false)
//     private LabOrder labOrder;

//     @Column(nullable = false)
//     private String testCode;

//     @Column(nullable = false)
//     private String value;

//     private String units;

//     @Column(columnDefinition = "JSON")
//     private String referenceRangeJson;

//     @Column(nullable = false)
//     private String flag;

//     @Column(nullable = false)
//     private LocalDateTime reportedAt;

//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "reported_by", nullable = false)
//     private User reportedBy;
// }


package com.HospitalManagement.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "lab_result")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LabResult {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long resultId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lab_order_id", nullable = false)
    private LabOrder labOrder;
    
    private String testCode;
    private String value;
    private String units;
    
    @Column(columnDefinition = "json")
    private String referenceRangeJson;
    
    private String flag;
    private LocalDateTime reportedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_by")
    private User reportedBy;
}
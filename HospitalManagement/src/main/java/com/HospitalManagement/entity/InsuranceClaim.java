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
// @Table(name = "insurance_claims")
// @Getter
// @Setter
// @NoArgsConstructor
// @AllArgsConstructor
// public class InsuranceClaim {

//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long claimId;

//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "invoice_id", nullable = false)
//     private Invoice invoice;

//     @Column(nullable = false)
//     private String payerId;

//     @Column(nullable = false, columnDefinition = "JSON")
//     private String claimPayloadJson;

//     private LocalDateTime submittedAt;

//     @Column(nullable = false)
//     private String status;

//     @Column(columnDefinition = "JSON")
//     private String responseJson;
// }


package com.HospitalManagement.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "insurance_claim")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InsuranceClaim {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long claimId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;
    
    private String payerId; // Could be an entity, but PDF specifies PayerID FK. Since we don't have a Payer entity, we'll keep it as String or Long if it maps to generic organisaion
    
    @Column(columnDefinition = "json")
    private String claimPayloadJson;
    
    private LocalDateTime submittedAt;
    private String status;
    
    @Column(columnDefinition = "json")
    private String responseJson;
}
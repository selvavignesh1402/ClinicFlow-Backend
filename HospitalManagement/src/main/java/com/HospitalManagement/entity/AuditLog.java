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
// @Table(name = "audit_logs")
// @Getter
// @Setter
// @NoArgsConstructor
// @AllArgsConstructor
// public class AuditLog {

//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long auditId;

//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "user_id", nullable = false)
//     private User user;

//     @Column(nullable = false)
//     private String action;

//     @Column(nullable = false)
//     private String resourceType;

//     @Column(nullable = false)
//     private String resourceId;

//     @Column(nullable = false, columnDefinition = "JSON")
//     private String detailsJson;

//     @Column(nullable = false)
//     private LocalDateTime timestamp;
// }


package com.HospitalManagement.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_log")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long auditId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    private String action;
    private String resourceType;
    private String resourceId;
    
    @Column(columnDefinition = "json")
    private String detailsJson;
    
    private LocalDateTime timestamp;
    
    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }
}
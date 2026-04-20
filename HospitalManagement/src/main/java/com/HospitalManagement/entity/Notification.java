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
// @Table(name = "notifications")
// @Getter
// @Setter
// @NoArgsConstructor
// @AllArgsConstructor
// public class Notification {

//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long notificationId;

//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "user_id")
//     private User user;

//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "patient_id")
//     private Patient patient;

//     @Column(nullable = false)
//     private String entityId;

//     @Column(nullable = false)
//     private String message;

//     @Column(nullable = false)
//     private String category;

//     @Column(nullable = false)
//     private String channel;

//     @Column(nullable = false)
//     private String severity;

//     @Column(nullable = false)
//     private LocalDateTime createdAt;

//     private LocalDateTime readAt;

//     @Column(nullable = false)
//     private String status;
// }


package com.HospitalManagement.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // Can be null
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    private Patient patient; // Can be null
    
    private String entityId;
    private String message;
    private String category;
    private String channel;
    private String severity;
    
    private LocalDateTime createdAt;
    private LocalDateTime readAt;
    private String status;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
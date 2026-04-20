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
// import java.time.LocalDate;
// import java.time.LocalDateTime;
// import lombok.AllArgsConstructor;
// import lombok.Getter;
// import lombok.NoArgsConstructor;
// import lombok.Setter;

// @Entity
// @Table(name = "tasks")
// @Getter
// @Setter
// @NoArgsConstructor
// @AllArgsConstructor
// public class Task {

//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long taskId;

//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "assigned_to", nullable = false)
//     private User assignedTo;

//     @Column(nullable = false)
//     private String relatedEntityId;

//     @Column(nullable = false)
//     private String description;

//     @Column(nullable = false)
//     private LocalDate dueDate;

//     @Column(nullable = false)
//     private String priority;

//     @Column(nullable = false)
//     private LocalDateTime createdAt;

//     private LocalDateTime completedAt;

//     @Column(nullable = false)
//     private String status;
// }


package com.HospitalManagement.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "task")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long taskId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to_fk", nullable = false)
    private User assignedTo;
    
    private String relatedEntityId;
    private String description;
    
    private LocalDateTime dueDate;
    private String priority;
    
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
    private String status;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
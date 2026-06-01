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
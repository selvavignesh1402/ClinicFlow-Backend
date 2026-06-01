package com.HospitalManagement.entity;

import com.HospitalManagement.enums.LabResultFlag;
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LabResultFlag flag;
    private LocalDateTime reportedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_by")
    private User reportedBy;
}
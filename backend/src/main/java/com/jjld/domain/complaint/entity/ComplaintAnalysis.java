package com.jjld.domain.complaint.entity;


import com.jjld.domain.complaint.entity.Enum.AnalysisPeriodType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        name = "complaint_analysis",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"periodType", "startDate", "endDate"}
                )
        }
)
public class ComplaintAnalysis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long analysisId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AnalysisPeriodType periodType;

    @Column(nullable = false, columnDefinition = "DATETIME")
    private LocalDate startDate;

    @Column(nullable = false, columnDefinition = "DATETIME")
    private LocalDate endDate;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
}

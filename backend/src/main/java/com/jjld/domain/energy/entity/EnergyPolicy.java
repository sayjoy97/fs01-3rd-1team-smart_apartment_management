package com.jjld.domain.energy.entity;

import com.jjld.domain.energy.entity.Enum.CompareBase;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "energy_policy")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnergyPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long policyId;

    @Column(nullable = false)
    private Integer sensitivityPercent; // 변화율 임계값(%)

    @Column(nullable = false)
    private Integer warningPercent; // 점검 권장 기준(%)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CompareBase compareBase;

    @Column(nullable = false)
    private LocalTime idleStartTime;

    @Column(nullable = false)
    private LocalTime idleEndTime;

    @Column(nullable = false)
    private Integer repeatLimit; // 반복 감지 기준 (회/24h)

    @Column(nullable = false)
    private Boolean ignoreSingleBreach; // 단발 초과 무시 여부

    @Column(nullable = false)
    private Boolean alertWarning;

    @Column(nullable = false)
    private Boolean alertCheck;

    @Column(nullable = false)
    private Integer costPerKwh;

    @Column(nullable = false)
    private Boolean isActive;

    @Column(nullable = false)
    private Double wasteThresholdKwh; // 낭비 임계값(kWh)

    @Column(nullable = false)
    private Integer repeatWindowHours; // 반복 감지 시간 범위(시간)

    @CreationTimestamp
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime updatedAt;
}
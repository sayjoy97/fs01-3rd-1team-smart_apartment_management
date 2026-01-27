package com.jjld.domain.noise.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "noise_policy")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoisePolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long policyId;

    @Column(nullable = false, unique = true, length = 100)
    private String policyName;

    @Column(nullable = false, columnDefinition = "DATETIME")
    private LocalTime dayStartTime;

    @Column(nullable = false, columnDefinition = "DATETIME")
    private LocalTime nightStartTime;

    @Column(nullable = false)
    private Integer soundLimit;

    private Integer timeThreshold; // 반복 판단 시간 창 (초 또는 분 단위)

    private Integer repeatLimit; // 허용 반복 횟수

    /**
     * 실제 카운트 값
     * → 정책 테이블보다는 로그/이력 테이블로 분리하는 것이 일반적
     */
    private Integer repeatCount;

    private Boolean isActive;

    @CreationTimestamp
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}

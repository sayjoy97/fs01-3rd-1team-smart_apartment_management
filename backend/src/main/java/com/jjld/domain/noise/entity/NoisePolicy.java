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

    @Column(nullable = false, columnDefinition = "TIME")
    private LocalTime dayStartTime;

    @Column(nullable = false, columnDefinition = "TIME")
    private LocalTime nightStartTime;

    @Column(nullable = false)
    private Integer soundLimit;

    @Column(nullable = false)
    private Integer repeatLimit; // 허용 반복 횟수

    @Column(nullable = false)
    private Integer timeThreshold; // 반복 판단 시간 창 (초 또는 분 단위)

    // 현재 적용 중인 정책 여부
    @Column(nullable = false)
    private Boolean isActive;

    @CreationTimestamp
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime createdAt;
}

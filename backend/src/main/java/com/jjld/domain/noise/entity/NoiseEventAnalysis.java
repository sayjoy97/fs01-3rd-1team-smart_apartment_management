package com.jjld.domain.noise.entity;

import com.jjld.domain.house.entity.House;
import com.jjld.domain.noise.entity.Enum.NoisePattern1;
import com.jjld.domain.noise.entity.Enum.NoisePattern2;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "noise_event_analysis")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoiseEventAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long analysisId;

    // NOISE_EVENT (1) ── (1) NOISE_EVENT_ANALYSIS
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "noise_event_id", unique = true, nullable = false)
    private NoiseEvent noiseEvent;

    // 1차 분류 (시스템)
    @Enumerated(EnumType.STRING)
    @Column(name = "noise_pattern_1", nullable = false, length = 30)
    private NoisePattern1 noisePattern1;

    // 2차 분류 (추정)
    @Enumerated(EnumType.STRING)
    @Column(name = "noise_pattern_2", nullable = false, length = 30)
    private NoisePattern2 noisePattern2;

    // 시간 창 내 반복 횟수
    @Column(nullable = false)
    private Integer repeatCount;

    // 정책 위반 의심 여부
    @Column(nullable = false)
    private Boolean vibrationDetected;

    // 정책 위반 의심 여부
    @Column(nullable = false)
    private Boolean policyBreak;

    // 분석 요약 설명
    @Column(nullable = false, length = 255)
    private String analysisNote;

    @CreationTimestamp
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime createdAt;
}

package com.jjld.domain.noise.entity;

import com.jjld.domain.house.entity.House;
import com.jjld.domain.noise.entity.Enum.NoiseEventStatus;
import com.jjld.domain.noise.entity.Enum.TimePeriod;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "noise_event")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoiseEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long noiseEventId;

    @ManyToOne(fetch = FetchType.LAZY)
    private NoiseSensor sensor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "house_id")
    private House house;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id")
    private NoisePolicy policy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TimePeriod timePeriod;

    @Column(nullable = false)
    private Integer soundLevel; // 추정 환산 dB

    @Column(nullable = false)
    private Integer eventCount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private NoiseEventStatus status;

    @Column(nullable = false)
    private Boolean policyBreak;

    @Column(nullable = false)
    private Boolean urgentBreak;

    @CreationTimestamp
    private LocalDateTime createdAt;
}

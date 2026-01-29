package com.jjld.domain.noise.entity;

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

    // NOISE_SENSOR (1) ── (N) NOISE_EVENT
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sensor_id", nullable = false)
    private NoiseSensor noiseSensor;

    @Column(nullable = false)
    private Integer soundLevel; // 추정 환산 dB

    @CreationTimestamp
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime createdAt;
}

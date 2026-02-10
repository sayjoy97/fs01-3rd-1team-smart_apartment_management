package com.jjld.domain.noise.entity;

import com.jjld.domain.house.entity.House;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "noise_habitual_zone")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoiseHabitualZone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long zoneId;

    // 기준 센서 (세대쌍 식별용)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sensor_id", nullable = false)
    private NoiseSensor sensor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "upper_house_id", nullable = false)
    private House upperHouse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lower_house_id", nullable = false)
    private House lowerHouse;

    // MONITORING / CLOSED
    @Column(nullable = false, length = 20)
    private String status;

    @Column(nullable = false)
    private LocalDateTime startedAt;

    private LocalDateTime endedAt;

    @CreationTimestamp
    private LocalDateTime createdAt;

    /* ===== 상태 변경 메서드 ===== */

    public void close() {
        this.status = "CLOSED";
        this.endedAt = LocalDateTime.now();
    }
}

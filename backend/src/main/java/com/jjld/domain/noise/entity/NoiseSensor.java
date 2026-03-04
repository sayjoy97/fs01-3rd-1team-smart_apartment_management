package com.jjld.domain.noise.entity;

import com.jjld.domain.house.entity.House;
import com.jjld.domain.noise.entity.Enum.SensorType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "noise_sensor")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoiseSensor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sensorId;

    // HOUSE (1) ── (N) NOISE_SENSOR
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "upper_house_id", nullable = false)
    private House upperHouse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lower_house_id", nullable = false)
    private House lowerHouse;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SensorType sensorType;

    @CreationTimestamp
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime installedAt;

    @Column(nullable = false)
    private Boolean isActive;
}

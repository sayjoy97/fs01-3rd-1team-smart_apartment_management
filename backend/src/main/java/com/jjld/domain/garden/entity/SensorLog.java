package com.jjld.domain.garden.entity;

import com.jjld.domain.garden.entity.Enum.SensorUnit;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "garden_sensor_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SensorLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sensorLogId;  // PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;  // 어떤 장치에서 수집된 데이터인지

    @Column(nullable = false)
    private Double value;  // 측정 값

    @Column(nullable = false, length = 20)
    private SensorUnit unit;  // 단위 (℃, %, lux 등)

    @CreationTimestamp
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime createdAt;  // 측정 시각
}

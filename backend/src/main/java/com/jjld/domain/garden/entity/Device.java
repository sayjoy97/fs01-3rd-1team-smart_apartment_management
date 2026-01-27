package com.jjld.domain.garden.entity;


import com.jjld.domain.garden.entity.Enum.DeviceState;
import com.jjld.domain.garden.entity.Enum.DeviceType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "garden_device")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long deviceId;  // PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "garden_id", nullable = false)
    private Garden garden;  // 어떤 정원에 속한 장치인지

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeviceType deviceType;  // 장치 타입 (센서/액추에이터)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeviceState state;  // 장치 상태
}
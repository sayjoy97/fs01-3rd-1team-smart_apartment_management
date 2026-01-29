package com.jjld.domain.parkingfee.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "parking_fee_setting")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParkingFeeSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "parking_fee_setting_id")
    private Long parkingFeeSettingId;

    // 기본 무료 시간 (분)
    @Column(nullable = false)
    private Integer baseTime;

    // 기본 요금
    @Column(nullable = false)
    private Integer baseCharge;

    // 단위 시간 (분)
    @Column(nullable = false)
    private Integer unitMinutes;

    // 단위 요금
    @Column(nullable = false)
    private Integer unitCharge;

    // 피크 요금 사용 여부
    @Column(nullable = false)
    private Boolean peakEnabled;

    // 피크 시간대 (시간만 쓰는 게 중요)
    private LocalTime peakStartTime;
    private LocalTime peakEndTime;

    private Integer peakUnitMinutes;
    private Integer peakUnitCharge;

    @Column(nullable = false)
    private Boolean active;

    private LocalDateTime appliedAt;
}


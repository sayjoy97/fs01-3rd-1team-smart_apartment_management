package com.jjld.domain.parkingfee.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

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

    // 단위 시간(분)
    @Column(nullable = false)
    private Integer unitMinutes;

    // 단위 요금
    @Column(nullable = false)
    private Integer unitCharge;

    // 피크 요금 사용 여부
    @Column(nullable = false)
    private Boolean peakEnabled;

    // 피크 시작시간
    @Column(columnDefinition = "TIME")
    private LocalTime peakStartTime;

    // 피크 종료시간
    @Column(columnDefinition = "TIME")
    private LocalTime peakEndTime;

    // 피크 단위 시간(분)
    private Integer peakUnitMinutes;

    // 피크시간 요금
    private Integer peakUnitCharge;

    // 피크 활성화
    @Column(nullable = false)
    private Boolean active;

    // 적용 날짜
    @CreationTimestamp
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime appliedAt;
}


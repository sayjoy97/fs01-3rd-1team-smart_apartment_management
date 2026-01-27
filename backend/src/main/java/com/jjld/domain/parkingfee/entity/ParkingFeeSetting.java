package com.jjld.domain.parkingfee.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "parking_fee_setting")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParkingFeeSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 기본 무료 시간 (분)
    @Column(nullable = false)
    private Integer baseTime;

    // 기본 요금
    @Column(nullable = false)
    private Integer baseCharge;

    // 피크 요금 사용 여부
    @Column(nullable = false, length = 1)
    private String useYN; // Y / N

    // 피크 시작 시간
    private LocalDateTime peakStartTime;

    // 피크 종료 시간
    private LocalDateTime peakEndTime;

    // 피크 단위 시간
    private Integer peakTime;

    // 피크 요금
    private Integer peakCharge;

    // 현재 적용 여부
    @Column(nullable = false)
    private Boolean isActive;

    // 설정 생성/변경 시각
    private LocalDateTime updatedAt;
}


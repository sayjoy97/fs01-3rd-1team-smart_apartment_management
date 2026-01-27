package com.jjld.domain.parkingfee.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "parking_fee_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParkingFeeHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 차량 번호
    @Column(nullable = false, length = 20)
    private String carNumber;

    // 등록 차량 여부
    @Column(nullable = false)
    private Boolean isRegistered;

    // 요금 부과 금액
    @Column(nullable = false)
    private Integer chargeAmount;

    // 입차 시각
    @Column(nullable = false)
    private LocalDateTime inTime;

    // 출차 시각
    @Column(nullable = false)
    private LocalDateTime outTime;

    // 정산 완료 여부
    @Column(nullable = false)
    private Boolean isPaid;

    // 정산 일자 (통계 기준용)
    @Column(nullable = false)
    private LocalDateTime chargeDate;
}

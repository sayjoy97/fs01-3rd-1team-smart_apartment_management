package com.jjld.domain.parkingfee.entity;

import com.jjld.domain.cargate.entity.ParkingSession;
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

    /** 어떤 주차 세션의 요금인가 */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parking_session_id", nullable = false)
    private ParkingSession parkingSession;

    /** 어떤 요금 정책을 사용했는가 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fee_setting_id", nullable = false)
    private ParkingFeeSetting feeSetting;

    /** 총 주차 시간 (분) */
    private Integer totalMinutes;

    /** 최종 요금 */
    private Integer totalCharge;

    /** 결제 여부 */
    private Boolean paid;

    /** 정산 시각 */
    private LocalDateTime chargedAt;
}


package com.jjld.domain.cargate.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "approved_car")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovedCar {

    @Id
    private Long id;  // vehicle.vehicle_id와 동일

    @MapsId
    @OneToOne
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle; // 승인 대상 차량

    private String approvalReason; // 승인 사유

    @Column(columnDefinition = "DATETIME")
    private LocalDate startAt; // 승인 시작일

    @Column(columnDefinition = "DATETIME")
    private LocalDate endAt; // 승인 종료일
}

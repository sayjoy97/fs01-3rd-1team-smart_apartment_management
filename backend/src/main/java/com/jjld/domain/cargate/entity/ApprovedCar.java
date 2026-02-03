package com.jjld.domain.cargate.entity;

import com.jjld.domain.cargate.entity.Enum.CurrentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

    @Enumerated(EnumType.STRING)
    private CurrentStatus currentStatus;

    @CreationTimestamp
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime createdAt; // 등록일

    @Column(columnDefinition = "DATETIME")
    private LocalDate startAt; // 승인 시작일

    @Column(columnDefinition = "DATETIME")
    private LocalDate endAt; // 승인 종료일

    public void refreshCurrentStatus() {
        LocalDate today = LocalDate.now();

        this.currentStatus =
                today.isBefore(startAt) ? CurrentStatus.BEFORE :
                        today.isAfter(endAt) ? CurrentStatus.END :
                                CurrentStatus.IN_PROGRESS;
    }

    @PrePersist
    @PreUpdate
    private void updateCurrentStatus() {
        refreshCurrentStatus();
    }
}

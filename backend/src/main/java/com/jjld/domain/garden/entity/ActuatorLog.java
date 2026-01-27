package com.jjld.domain.garden.entity;

import com.jjld.domain.admin.entity.Admin;
import com.jjld.domain.garden.entity.Enum.ActionType;
import com.jjld.domain.garden.entity.Enum.ControlType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "garden_actuator_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActuatorLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long actuatorLogId;  // PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;  // 어떤 장치를 제어했는지

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private Admin admin;  // 수동 제어한 관리자 (AUTO면 null)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ControlType controlType;  // AUTO / MANUAL / SCHEDULE

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActionType action;  // ON / OFF

    @Column(length = 255)
    private String reason;  // 제어 사유

    @CreationTimestamp
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime createdAt;  // 기록 시각
}

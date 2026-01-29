package com.jjld.domain.noise.entity;

import com.jjld.domain.admin.entity.Admin;
import com.jjld.domain.noise.entity.Enum.ProcessStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "noise_event_process")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoiseEventProcess {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long processId;

    // NOISE_EVENT (1) ── (1) NOISE_EVENT_PROCESS
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "noise_event_id", nullable = false)
    private NoiseEvent noiseEvent;

    // NOISE_POLICY (1) ── (N) NOISE_EVENT_PROCESS
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", nullable = false)
    private NoisePolicy noisePolicy;

    // 관리자 처리 상태
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProcessStatus status;

    // 즉시 처리 필요 여부 (알림 / 강조용)
    @Column(nullable = false)
    private Boolean urgentBreak;

    // 승인 / 보류 처리한 관리자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private Admin admin;

    // 관리자 메모
    @Column(columnDefinition = "TEXT")
    private String adminMemo;

    @CreationTimestamp
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime createdAt;
}

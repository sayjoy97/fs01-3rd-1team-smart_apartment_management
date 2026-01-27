package com.jjld.domain.noise.entity;

import com.jjld.domain.admin.entity.Admin;
import com.jjld.domain.noise.entity.Enum.AdminAction;
import jakarta.persistence.*;
import lombok.*;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "noise_event_id")
    private NoiseEvent noiseEvent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private Admin admin;

    @Enumerated(EnumType.STRING)
    @Column(name = "admin_action", nullable = false)
    private AdminAction adminAction;

    @Column(name = "admin_memo")
    private String adminMemo;

    @Column(columnDefinition = "DATETIME")
    private LocalDateTime processedAt;
}

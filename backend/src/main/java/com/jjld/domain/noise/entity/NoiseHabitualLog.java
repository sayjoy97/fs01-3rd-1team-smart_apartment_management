package com.jjld.domain.noise.entity;

import com.jjld.domain.admin.entity.Admin;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "noise_habitual_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoiseHabitualLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long logId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id", nullable = false)
    private NoiseHabitualZone zone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private Admin admin;

    // REGISTER / NOTIFY / CLOSE
    @Column(nullable = false, length = 30)
    private String action;

    @Column(columnDefinition = "TEXT")
    private String memo;

    @CreationTimestamp
    private LocalDateTime createdAt;
}

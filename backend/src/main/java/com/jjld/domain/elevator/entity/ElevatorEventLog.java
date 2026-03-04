package com.jjld.domain.elevator.entity;

import com.jjld.domain.elevator.entity.Enum.ElevatorEventType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "elevator_event_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ElevatorEventLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long logId;  // PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "elevator_id", nullable = false)
    private Elevator elevator;  // 어떤 엘리베이터에서 발생한 이벤트인지

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ElevatorEventType eventType;  // 이벤트 종류

    private Integer floor;  // 관련 층 (없으면 null 가능)

    @Column(nullable = false, length = 255)
    private String message;  // 상세 설명

    @CreationTimestamp
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime createdAt;  // 발생 시각
}

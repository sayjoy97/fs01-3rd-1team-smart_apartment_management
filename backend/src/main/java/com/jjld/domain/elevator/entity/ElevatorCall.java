package com.jjld.domain.elevator.entity;

import com.jjld.domain.elevator.entity.Enum.CallDirection;
import com.jjld.domain.elevator.entity.Enum.CallStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "elevator_call")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ElevatorCall {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long callId;  // PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "elevator_id", nullable = false)
    private Elevator elevator;  // 어떤 엘리베이터에 할당된 호출인지

    @Column(nullable = false)
    private Integer callFloor;  // 호출 층

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CallDirection direction;  // 호출 방향

    @CreationTimestamp
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime createdAt;  // 호출 시간

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CallStatus status;  // 처리 상태
}

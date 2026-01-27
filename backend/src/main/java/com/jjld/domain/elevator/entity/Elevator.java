package com.jjld.domain.elevator.entity;

import com.jjld.domain.elevator.entity.Enum.CallMethod;
import com.jjld.domain.elevator.entity.Enum.Direction;
import com.jjld.domain.elevator.entity.Enum.DoorStatus;
import com.jjld.domain.elevator.entity.Enum.ElevatorState;
import com.jjld.domain.house.entity.House;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "elevator")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Elevator {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long elevatorId;   // PK

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "house_id", nullable = false)
    private House house;  // 호출한 세대 (최근 호출 기준)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CallMethod callMethod;  // 호출 방식

    @Column(nullable = false)
    private Integer currentFloor;  // 현재 층

    private Integer targetFloor;  // 목표 층 (없으면 null)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Direction direction;  // 이동 방향

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ElevatorState state;  // 엘리베이터 상태

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DoorStatus doorStatus;  // 문 상태

    @UpdateTimestamp
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime updatedAt;  // 마지막 상태 갱신 시각
}

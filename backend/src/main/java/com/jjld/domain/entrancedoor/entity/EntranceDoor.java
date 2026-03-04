package com.jjld.domain.entrancedoor.entity;

import com.jjld.domain.entrancedoor.entity.Enum.FrontDoorStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "entrance_door")
public class EntranceDoor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long doorId;

    @Column(nullable = false, unique = true)
    private Integer houseDong;

    @Enumerated(EnumType.STRING)
    private FrontDoorStatus status = FrontDoorStatus.CLOSED;

    private LocalDateTime lastStatusChangedAt;

    // 문 상태 변경
    public void changeStatus(FrontDoorStatus status){
        this.status = status;
        this.lastStatusChangedAt = LocalDateTime.now();
    }
}

package com.jjld.domain.cargate.entity;

import com.jjld.domain.cargate.entity.Enum.ParkingStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "parking_session")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParkingSession {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long parkingSessionId; // 주차 세션 식별자

    @ManyToOne(fetch = FetchType.LAZY)
    private Vehicle vehicle; // 어떤 차량의 주차인지

    @ManyToOne(fetch = FetchType.LAZY)
    private CarGate entryCarGate; // 입차한 게이트

    @ManyToOne(fetch = FetchType.LAZY)
    private CarGate exitCarGate; // 출차한 게이트

    @Column(columnDefinition = "DATETIME")
    private LocalDateTime entryAt; // 입차시간

    @Column(columnDefinition = "DATETIME")
    private LocalDateTime exitAt; // 출차시간

    @Enumerated(EnumType.STRING)
    private ParkingStatus status;  // 현재 상태(IN/OUT)

    //입차 시
    public static ParkingSession entry(Vehicle vehicle, CarGate carGate, LocalDateTime time) {
        ParkingSession ps = new ParkingSession();
        ps.vehicle = vehicle;
        ps.entryCarGate = carGate;
        ps.entryAt = time;
        ps.status = ParkingStatus.IN;
        return ps;
    }

    // 출차시
    public void exit(CarGate carGate, LocalDateTime time) {
        this.exitCarGate = carGate;
        this.exitAt = time;
        this.status = ParkingStatus.OUT;
    }
}

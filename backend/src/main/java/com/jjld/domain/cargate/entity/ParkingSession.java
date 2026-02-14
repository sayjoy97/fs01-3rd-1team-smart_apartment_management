package com.jjld.domain.cargate.entity;

import com.jjld.domain.cargate.entity.Enum.ParkingStatus;
import com.jjld.domain.parkingfee.entity.ParkingFeeHistory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle; // 어떤 차량의 주차인지

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entry_cargate_id", referencedColumnName = "cargate_id")
    private Cargate entryCargate; // 입차한 게이트

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exit_cargate_id", referencedColumnName = "cargate_id")
    private Cargate exitCargate; // 출차한 게이트

    @Column(columnDefinition = "DATETIME")
    private LocalDateTime entryAt; // 입차시간

    @Column(columnDefinition = "DATETIME")
    private LocalDateTime exitAt; // 출차시간

    @Enumerated(EnumType.STRING)
    private ParkingStatus status;  // 현재 상태(IN/OUT)

    @OneToOne(mappedBy = "parkingSession", fetch = FetchType.LAZY)
    private ParkingFeeHistory parkingFeeHistory;

    @OneToMany(mappedBy = "parkingSession", fetch = FetchType.LAZY)
    private List<CargateEventLog> cargateEventLogs = new ArrayList<>();

    //입차 시
    public static ParkingSession entry(Vehicle vehicle, Cargate carGate, LocalDateTime time) {
        return ParkingSession.builder()
                .vehicle(vehicle)
                .entryCargate(carGate)
                .entryAt(time)
                .status(ParkingStatus.IN)
                .build();
    }

    // 출차시
    public void exit(Cargate carGate, LocalDateTime time) {
        this.exitCargate = carGate;
        this.exitAt = time;
        this.status = ParkingStatus.OUT;
    }
}

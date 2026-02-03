package com.jjld.domain.cargate.entity;

import com.jjld.domain.cargate.entity.Enum.VehicleType;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "vehicle",
        uniqueConstraints = @UniqueConstraint(columnNames = "plate_number")
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long vehicleId; // 차량테이블 기본키

    @Column(name = "plate_number", nullable = false)
    private String plateNumber; // ocr로 추출된 차량 번호판

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleType vehicleType; // 차량 유형

    @OneToMany(mappedBy = "vehicle")
    @ToString.Exclude
    private List<ParkingSession> parkingSessions = new ArrayList<>(); // 과거~현재 주차이력

    // 과거~현재 들어온 차량이 아닌 차량 미리등록
    public Vehicle(String plateNumber, VehicleType vehicleType) {
        this.plateNumber = plateNumber;
        this.vehicleType = vehicleType;
    }
}
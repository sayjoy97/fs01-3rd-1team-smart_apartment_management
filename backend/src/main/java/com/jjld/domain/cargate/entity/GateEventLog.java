package com.jjld.domain.cargate.entity;

import com.jjld.domain.cargate.entity.Enum.GateType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "gate_event_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GateEventLog {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gateEventId; // 이벤트 로그 식별자

    @ManyToOne(fetch = FetchType.LAZY)
    private Gate gate; // 발생 게이트 위치

    @ManyToOne(fetch = FetchType.LAZY)
    private Vehicle vehicle; // OCR결과 매핑된 차량, OCR실패시 null

    @Enumerated(EnumType.STRING)
    private GateType gateType; // 이벤트 성격(ENTRY/EXIT)

    @Column(columnDefinition = "DATETIME")
    private LocalDateTime eventAt; // 카메라 촬영시간

    private String imagePath; // 촬영된 이미지 경로
}

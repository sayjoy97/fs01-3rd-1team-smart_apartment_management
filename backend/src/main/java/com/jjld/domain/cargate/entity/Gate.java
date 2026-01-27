package com.jjld.domain.cargate.entity;

import com.jjld.domain.cargate.entity.Enum.GateType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "gate")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Gate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gateId; // 게이트 식별자

    private String gateName; // 게이트 이름

    @Enumerated(EnumType.STRING)
    private GateType gateType; // ENTRY / EXIT
}

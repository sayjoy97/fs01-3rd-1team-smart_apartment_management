package com.jjld.domain.doorgate.entity;

import com.jjld.domain.admin.entity.Admin;
import com.jjld.domain.doorgate.entity.Enum.FailReason;
import com.jjld.domain.house.entity.EntranceCard;
import com.jjld.domain.house.entity.House;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "door_gate_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoorGateLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accessLogId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "house_id", nullable = false)
    private House house;

    @ManyToOne
    @JoinColumn(name = "card_id")
    private EntranceCard card;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private Admin admin;

    @CreationTimestamp
    @Column(nullable = false, columnDefinition = "DATETIME")
    private LocalDateTime accessedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccessType accessType;

//    @Enumerated(EnumType.STRING)
//    private DeviceType deviceType;

    private Boolean outcome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FailReason failReason;


}

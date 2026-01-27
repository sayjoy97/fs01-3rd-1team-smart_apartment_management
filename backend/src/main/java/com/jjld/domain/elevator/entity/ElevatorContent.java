package com.jjld.domain.elevator.entity;

import com.jjld.domain.admin.entity.Admin;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "elevator_content")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ElevatorContent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long contentId;  // PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "elevator_id", nullable = false)
    private Elevator elevator;  // 어느 엘리베이터에 표시되는지

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private Admin admin;  // 콘텐츠 등록 관리자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id")
    private Notice notice;  // 공지 콘텐츠 (없을 수 있음)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "advertisement_id")
    private Advertisement advertisement;  // 광고 콘텐츠 (없을 수 있음)

    @Column(nullable = false, columnDefinition = "DATETIME")
    private LocalDateTime contentStartDate;  // 노출 시작일

    @Column(nullable = false, columnDefinition = "DATETIME")
    private LocalDateTime contentEndDate;  // 노출 종료일

    @Column(nullable = false)
    private Integer exposureTime;  // 노출 시간 (초 단위)

    @Column(nullable = false)
    private Boolean isExposure;  // 현재 노출 활성화 여부
}

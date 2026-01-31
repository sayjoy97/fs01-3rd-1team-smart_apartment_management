package com.jjld.domain.garden.entity;

import com.jjld.domain.admin.entity.Admin;
import com.jjld.domain.garden.entity.Enum.Priority;
import com.jjld.domain.garden.entity.Enum.ScheduleState;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "garden_work_schedule")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scheduleId;  // PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "garden_id", nullable = false)
    private Garden garden;  // 대상 정원

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private Admin admin;  // 작업 등록 관리자

    @Column(nullable = false, length = 200)
    private String workTitle;  // 작업 제목

    @Column(columnDefinition = "TEXT")
    private String workContent;  // 작업 내용

    @Column(nullable = false)
    private LocalDateTime workStartDate;  // 시작일

    @Column(nullable = false)
    private LocalDateTime workEndDate;  // 종료일

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ScheduleState state = ScheduleState.SCHEDULED;  // 진행 상태

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority;  // 우선순위
}
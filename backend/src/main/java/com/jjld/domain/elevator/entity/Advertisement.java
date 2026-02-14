package com.jjld.domain.elevator.entity;

import com.jjld.domain.admin.entity.Admin;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "advertisement")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Advertisement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long advertisementId;  // PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private Admin admin;  // 광고 등록 관리자

    @Column(nullable = false, length = 200)
    private String advertisementTitle;  // 광고 제목

    @Column(nullable = false, columnDefinition = "TEXT")
    private String advertisementContent;  // 광고 내용

    @Column(nullable = false, columnDefinition = "DATETIME")
    private LocalDate advertisementStartDate;  // 계약 시작일

    @Column(nullable = false, columnDefinition = "DATETIME")
    private LocalDate advertisementEndDate;  // 계약 종료일
}

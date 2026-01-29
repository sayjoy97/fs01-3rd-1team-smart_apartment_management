package com.jjld.domain.complex.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "apartment_complex")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Complex {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;  // 단지명

    @Column(nullable = false)
    private Integer totalHouseholds;  // 총 세대수

    @Column(nullable = false, length = 255)
    private String address;  // 주소

    @Column(length = 20)
    private String phoneNumber;  // 대표 전화번호

    @Column(length = 100)
    private String email;  // 대표 이메일

    @Column(nullable = false, columnDefinition = "DATETIME")
    private LocalDate completionDate;  // 준공일

    @Column(nullable = false)
    private Integer buildingCount;  // 동 수

    @Column(nullable = false)
    private Integer maxFloor;  // 최고 층 수

    private Integer parkingCapacity;  // 주차 가능 대수

    @CreationTimestamp
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime createdAt;  // 생성일

    @UpdateTimestamp
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime updatedAt;  // 수정일
}

package com.jjld.domain.garden.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "garden")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Garden {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gardenId;  // PK

    @Column(nullable = false, length = 100)
    private String name;  // 정원 이름

    @Column(nullable = false, length = 255)
    private String location;  // 위치 정보

    private Float areaSize;  // 면적 (㎡)

    @Column(nullable = false)
    private Boolean isWatering = false;  // 자동 급수 여부 (초기값 false)

    @CreationTimestamp
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime createdAt;  // 등록일
}

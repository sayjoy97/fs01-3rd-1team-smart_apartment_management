package com.jjld.domain.entrancedoor.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "entrance_open_request")
public class EntranceOpenRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long requestId;

    @Column(nullable = false)
    private Integer houseDong;

    @Column(nullable = false)
    private LocalDateTime requestedAt;

    private Boolean processed;

    private LocalDateTime processedAt;
}

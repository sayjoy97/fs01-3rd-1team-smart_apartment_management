package com.jjld.domain.noise.entity;

import com.jjld.domain.noise.entity.Enum.NoisePattern;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "noise_event_analysis")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoiseEventAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long analysisId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "noise_event_id")
    private NoiseEvent noiseEvent;

    @Enumerated(EnumType.STRING)
    @Column(name = "noise_pattern", nullable = false)
    private NoisePattern noisePattern;

    @Column(nullable = false)
    private Boolean impactDetected;

    @Column(nullable = false)
    private Boolean vibDetected;

    @Column(nullable = false)
    private Boolean soundChangeDetected;

    private String analysisNote;
}

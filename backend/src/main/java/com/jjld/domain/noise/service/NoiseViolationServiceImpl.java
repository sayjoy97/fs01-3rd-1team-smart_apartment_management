package com.jjld.domain.noise.service;

import com.jjld.domain.noise.entity.Enum.NoisePattern1;
import com.jjld.domain.noise.entity.Enum.NoisePattern2;
import com.jjld.domain.noise.entity.Enum.SensorType;
import com.jjld.domain.noise.entity.NoiseEvent;
import com.jjld.domain.noise.entity.NoiseEventAnalysis;
import com.jjld.domain.noise.entity.NoisePolicy;
import com.jjld.domain.noise.repository.NoiseEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NoiseViolationServiceImpl implements NoiseViolationService {
    private final NoisePolicyService noisePolicyService;
    private final NoiseEventRepository noiseEventRepository;
    // 소음 이벤트 분석
    // 판단기준: 소음강도기준 초과여부 >> 반복 횟수기준 초과여부
    @Override
    public NoiseEventAnalysis analyzeNoiseEvent(NoiseEvent noiseEvent, int repeatCount) {
        // 현재 적용 중인 소음 정책 조회
        NoisePolicy policy = noisePolicyService.findActiveNoisePolicy();
        // 마이크 기준 초과 여부 (필수 조건)
        boolean soundExceeded = noiseEvent.getSoundLevel() >= policy.getSoundLimit();
        // 같은 시점에 진동 센서 감지 여부 (확인 증거)
        boolean vibrationDetected = existsVibrationEventAround(noiseEvent, 1);
        // 최종 urgent 판단
        boolean urgentBreak = soundExceeded && vibrationDetected;
        String analysisNote = createAnalysisNote(
                policy,
                noiseEvent.getSoundLevel(),
                soundExceeded,
                vibrationDetected,
                urgentBreak
        );
        return NoiseEventAnalysis.builder()
                .noiseEvent(noiseEvent)
                .noisePattern1(urgentBreak
                        ? NoisePattern1.IMPACT
                        : NoisePattern1.UNCERTAIN)
                .noisePattern2(NoisePattern2.UNCLASSIFIED)
                .repeatCount(repeatCount)
                .policyBreak(urgentBreak)
                .analysisNote(analysisNote)
                .build();
    }

    // 특정 이벤트 기준 ±seconds 내
    // Piezo 또는 SW-420 이벤트 존재 여부 확인
    private boolean existsVibrationEventAround(NoiseEvent event, int seconds) {
        LocalDateTime from = event.getCreatedAt().minusSeconds(seconds);
        LocalDateTime to   = event.getCreatedAt().plusSeconds(seconds);
        return noiseEventRepository.existsByNoiseSensorAndNoiseSensor_SensorTypeInAndCreatedAtBetween(
                        event.getNoiseSensor(), List.of(SensorType.PIEZO, SensorType.SW_420),
                        from, to);
    }

    // 정책 위반 사유 설명 문장 생성
    // "설정된 기준(강도 70dB)을 초과했습니다."
    // "설정된 기준(반복 5회)을 초과했습니다."
    // "설정된 기준(강도 70dB, 반복 5회)을 초과했습니다."
    private String createAnalysisNote(
            NoisePolicy policy,
            int soundLevel,
            boolean soundExceeded,
            boolean vibrationDetected,
            boolean urgentBreak
    ) {
        if (urgentBreak) {
            return String.format(
                    "마이크 기준(%dB) 초과 + 진동 감지로 층간소음 의심 이벤트로 판단되었습니다. (측정값: %dB)",
                    policy.getSoundLimit(), soundLevel
            );
        }
        if (soundExceeded) {
            return String.format(
                    "소음 강도는 기준(%dB)을 초과했으나, 진동이 감지되지 않아 긴급 이벤트로 분류되지 않았습니다. (측정값: %dB)",
                    policy.getSoundLimit(), soundLevel
            );
        }
        // 위반 아님
        return String.format(
                "정책 위반 기준에 해당하지 않습니다. (강도 %dB)",
                soundLevel
        );
    }
}
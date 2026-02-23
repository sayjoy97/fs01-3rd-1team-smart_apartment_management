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
    public NoiseEventAnalysis analyzeNoiseEvent(NoiseEvent noiseEvent, int repeatCount,  NoisePolicy policy) {
        boolean isNight = !noisePolicyService.isDayTime(noiseEvent.getCreatedAt().toLocalTime());
        // 마이크 기준 초과 여부 (필수 조건)
        boolean soundExceeded = noiseEvent.getSoundLevel() >= policy.getSoundLimit();
        // 같은 시점에 진동 센서 감지 여부 (확인 증거)
        boolean vibrationDetected = existsVibrationEventAround(noiseEvent, 1);

        boolean repeatExceeded = repeatCount >= policy.getRepeatLimit();
        // 최종 urgent 판단
        boolean policyBreak = soundExceeded && (repeatExceeded || isNight || vibrationDetected);

        String analysisNote = createAnalysisNote(
                policy,
                noiseEvent.getSoundLevel(),
                repeatCount,
                isNight,
                soundExceeded,
                repeatExceeded,
                vibrationDetected,
                policyBreak
        );
        return NoiseEventAnalysis.builder()
                .noiseEvent(noiseEvent)
                .noisePattern1(policyBreak
                        ? NoisePattern1.IMPACT
                        : NoisePattern1.UNCERTAIN)
                .noisePattern2(NoisePattern2.UNCLASSIFIED)
                .repeatCount(repeatCount)
                .policyBreak(policyBreak)
                .analysisNote(analysisNote)
                .vibrationDetected(vibrationDetected)
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
            int repeatCount,
            boolean isNight,
            boolean soundExceeded,
            boolean repeatExceeded,
            boolean vibrationDetected,
            boolean policyBreak) {
        if (!soundExceeded) {
            return String.format("정책 위반 기준에 해당하지 않습니다. (강도 %ddB)", soundLevel);
        }

        // soundExceeded == true
        String base = String.format("소음 강도 기준(%ddB) 초과 (측정값: %ddB)", policy.getSoundLimit(), soundLevel);

        StringBuilder sb = new StringBuilder(base);

        if (isNight) sb.append(", 야간 시간대");
        if (repeatExceeded) sb.append(String.format(", %d분 내 반복 %d회(기준 %d회)", policy.getTimeThreshold(), repeatCount, policy.getRepeatLimit()));
        if (vibrationDetected) sb.append(", 진동 감지");

        if (policyBreak) sb.append("로 정책 위반 의심 이벤트로 판단되었습니다.");
        else sb.append("이나 정책 위반으로 분류되지는 않았습니다.");

        return sb.toString();
    }
}
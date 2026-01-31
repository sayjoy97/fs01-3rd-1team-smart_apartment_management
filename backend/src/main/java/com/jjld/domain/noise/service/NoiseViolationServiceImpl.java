package com.jjld.domain.noise.service;

import com.jjld.domain.noise.entity.Enum.NoisePattern1;
import com.jjld.domain.noise.entity.Enum.NoisePattern2;
import com.jjld.domain.noise.entity.NoiseEvent;
import com.jjld.domain.noise.entity.NoiseEventAnalysis;
import com.jjld.domain.noise.entity.NoisePolicy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class NoiseViolationServiceImpl implements NoiseViolationService {
    private final NoisePolicyService noisePolicyService;
    // 소음 이벤트 분석
    // 판단기준: 소음강도기준 초과여부 >> 반복 횟수기준 초과여부
    @Override
    public NoiseEventAnalysis analyzeNoiseEvent(NoiseEvent noiseEvent, int repeatCount) {
        // 현재 적용 중인 소음 정책 조회
        NoisePolicy policy = noisePolicyService.findActiveNoisePolicy();
        // 소음 강도 기준 초과 여부
        boolean soundLimitExceeded = noiseEvent.getSoundLevel() >= policy.getSoundLimit();
        // 반복 횟수 기준 초과 여부
        boolean repeatLimitExceeded = repeatCount >= policy.getRepeatLimit();
        // 정책 위반 여부 판단
        boolean policyBreak = soundLimitExceeded || repeatLimitExceeded;
        // 위반 사유 문장 생성
        String analysisNote = createAnalysisNote(
                policy,
                noiseEvent.getSoundLevel(),
                repeatCount,
                soundLimitExceeded,
                repeatLimitExceeded
        );
        // 분석 결과 객체 생성
        return NoiseEventAnalysis.builder()
                .noiseEvent(noiseEvent)
                .noisePattern1(NoisePattern1.UNCERTAIN)   // 1차 패턴은 시스템 분류 결과로 교체 가능
                .noisePattern2(NoisePattern2.UNCLASSIFIED)
                .repeatCount(repeatCount)
                .policyBreak(policyBreak)
                .analysisNote(analysisNote)
                .build();
    }

    // 정책 위반 사유 설명 문장 생성
    // "설정된 기준(강도 70dB)을 초과했습니다."
    // "설정된 기준(반복 5회)을 초과했습니다."
    // "설정된 기준(강도 70dB, 반복 5회)을 초과했습니다."
    private String createAnalysisNote(
            NoisePolicy policy,
            int soundLevel,
            int repeatCount,
            boolean soundExceeded,
            boolean repeatExceeded
    ) {
        // 둘 다 초과한 경우
        if (soundExceeded && repeatExceeded) {
            return String.format(
                    "설정된 기준(강도 %ddB, 반복 %d회)을 초과했습니다. (측정값: %ddB, %d회)",
                    policy.getSoundLimit(),
                    policy.getRepeatLimit(),
                    soundLevel,
                    repeatCount
            );
        }
        // 소음 강도만 초과한 경우
        if (soundExceeded) {
            return String.format(
                    "설정된 기준(강도 %ddB)을 초과했습니다. (측정값: %ddB)",
                    policy.getSoundLimit(),
                    soundLevel
            );
        }
        // 반복 횟수만 초과한 경우
        if (repeatExceeded) {
            return String.format(
                    "설정된 기준(반복 %d회)을 초과했습니다. (측정값: %d회)",
                    policy.getRepeatLimit(),
                    repeatCount
            );
        }
        // 위반 아님
        return String.format(
                "정책 위반 기준에 해당하지 않습니다. (강도 %ddB / 반복 %d회)",
                soundLevel,
                repeatCount
        );
    }
}
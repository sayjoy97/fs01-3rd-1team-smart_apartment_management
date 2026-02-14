package com.jjld.domain.noise.dao;

import com.jjld.domain.noise.entity.Enum.ProcessStatus;
import com.jjld.domain.noise.entity.NoiseEvent;
import com.jjld.domain.noise.entity.NoisePolicy;
import com.jjld.domain.noise.repository.NoiseEventAnalysisRepository;
import com.jjld.domain.noise.repository.NoiseEventProcessRepository;
import com.jjld.domain.noise.repository.NoiseEventRepository;
import com.jjld.domain.noise.repository.NoisePolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class NoiseDashboardDAOImpl implements NoiseDashboardDAO {
    private final NoiseEventRepository noiseEventRepository;
    private final NoiseEventAnalysisRepository noiseEventAnalysisRepository;
    private final NoiseEventProcessRepository noiseEventProcessRepository;
    private final NoisePolicyRepository noisePolicyRepository;

    // 오늘 발생한 소음 이벤트
    @Override
    public long findNoiseEventToday(LocalDate today) {
        // 오늘 00:00:00
        LocalDateTime start = today.atStartOfDay();
        // 내일 00:00:00 (between 조회용)
        LocalDateTime end = today.plusDays(1).atStartOfDay();
        return noiseEventRepository.countByCreatedAtBetween(start, end);
    }
    // 오늘 정책 위반의심 소음 이벤트
    @Override
    public long findPolicyBreakEventToday(LocalDate today) {
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay();
        return noiseEventAnalysisRepository
                .countByPolicyBreakTrueAndCreatedAtBetween(start, end);
    }
    // 승인 대기 중인 소음 이벤트
    @Override
    public long countWaitingNoiseEvent() {
        // ProcessStatus.PENDING (승인보류) 상태인 이벤트만 카운트
        return noiseEventProcessRepository
                .countByStatus(ProcessStatus.UNPROCESSED);
    }
    // 현재 시간대 판단(현재 활성화된 소음 정책 기준으로)
    @Override
    public String findCurrentTimeZone() {
        // 현재 적용 중인 정책 조회 (항상 1개 존재)
        NoisePolicy policy = noisePolicyRepository
                .findByIsActiveTrue()
                .orElseThrow(() -> new IllegalStateException("활성화된 소음 정책이 없습니다."));
        LocalTime now = LocalTime.now();
        // 주간 시작 ~ 야간 시작 사이는 주간
        if (now.isAfter(policy.getDayStartTime())
                && now.isBefore(policy.getNightStartTime())) {
            return "주간";
        }
        // 그 외 시간은 야간
        return "야간";
    }
}

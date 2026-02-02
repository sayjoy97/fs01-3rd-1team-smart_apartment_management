package com.jjld.domain.noise.service;

import com.jjld.domain.noise.dao.NoiseDashboardDAO;
import com.jjld.domain.noise.dto.NoiseDashboardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoiseDashboardServiceImpl implements NoiseDashboardService {
    private final NoiseDashboardDAO noiseDashboardDAO;
    @Override
    public NoiseDashboardResponse getDashboard() {
        // 오늘 날짜 기준
        LocalDate today = LocalDate.now();
        // DAO에서 숫자 요약 조회
        long todayEventCount = noiseDashboardDAO.findNoiseEventToday(today);
        long todayPolicyBreakCount = noiseDashboardDAO.findPolicyBreakEventToday(today);
        long waitingEventCount = noiseDashboardDAO.countWaitingNoiseEvent();
        String currentTimeZone = noiseDashboardDAO.findCurrentTimeZone();
        // DTO 조립
        return NoiseDashboardResponse.builder()
                .todayEventCount(todayEventCount)
                .todayPolicyBreakCount(todayPolicyBreakCount)
                .pendingEventCount(waitingEventCount)
                .currentTimeZone(currentTimeZone)
                .build();
    }
}

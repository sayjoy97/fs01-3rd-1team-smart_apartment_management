package com.jjld.domain.noise.service;

import com.jjld.domain.admin.entity.Admin;
import com.jjld.domain.admin.repository.AdminRepository;
import com.jjld.domain.noise.entity.NoiseEventProcess;
import com.jjld.domain.noise.entity.NoiseHabitualLog;
import com.jjld.domain.noise.entity.NoiseHabitualZone;
import com.jjld.domain.noise.entity.NoiseSensor;
import com.jjld.domain.noise.repository.NoiseEventProcessRepository;
import com.jjld.domain.noise.repository.NoiseHabitualLogRepository;
import com.jjld.domain.noise.repository.NoiseHabitualZoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class NoiseHabitualServiceImpl implements NoiseHabitualService{
    private final NoiseHabitualZoneRepository zoneRepository;
    private final NoiseHabitualLogRepository logRepository;
    private final NoiseEventProcessRepository processRepository;
    private final AdminRepository adminRepository;

    @Override
    public void registerZone(Long noiseEventProcessId, Long adminId, String memo) {
        // 1. Process 조회
        NoiseEventProcess process = processRepository.findById(noiseEventProcessId)
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 소음 이벤트 처리 ID입니다.")
                );
        NoiseSensor sensor = process.getNoiseEvent().getNoiseSensor();

        // 2. 이미 모니터링 중인 상습구간이 있으면 등록 불가
        if (zoneRepository.existsBySensorAndStatus(sensor, "MONITORING")) {
            throw new IllegalStateException("이미 모니터링 중인 상습 구간이 존재합니다.");
        }

        // 3. 관리자 조회
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 관리자입니다.")
                );

        // 4. 상습 구간 생성
        NoiseHabitualZone zone = NoiseHabitualZone.builder()
                .sensor(sensor)
                .upperHouse(sensor.getUpperHouse())
                .lowerHouse(sensor.getLowerHouse())
                .status("MONITORING")
                .startedAt(LocalDateTime.now())
                .build();

        zoneRepository.save(zone);

        // 5. 로그 기록 (REGISTER)
        NoiseHabitualLog log = NoiseHabitualLog.builder()
                .zone(zone)
                .admin(admin)
                .action("REGISTER")
                .memo(
                        memo != null && !memo.isBlank()
                                ? memo
                                : "관리자 수동 상습 구간 등록"
                )
                .build();

        logRepository.save(log);
    }

    @Override
    public void closeZone(Long zoneId, Long adminId, String memo) {
        // 1. 상습 구간 조회
        NoiseHabitualZone zone = zoneRepository.findById(zoneId)
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 상습 구간입니다.")
                );

        // 2. 이미 종료된 경우 차단
        if ("CLOSED".equals(zone.getStatus())) {
            throw new IllegalStateException("이미 종료된 상습 구간입니다.");
        }

        // 3. 관리자 조회
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 관리자입니다.")
                );

        // 4. 상태 변경
        zone.close(); // status = CLOSED, endedAt 세팅

        // 5. 로그 기록 (CLOSE)
        NoiseHabitualLog log = NoiseHabitualLog.builder()
                .zone(zone)
                .admin(admin)
                .action("CLOSE")
                .memo(
                        memo != null && !memo.isBlank()
                                ? memo
                                : "관리자에 의해 모니터링 종료"
                )
                .build();

        logRepository.save(log);
    }
}

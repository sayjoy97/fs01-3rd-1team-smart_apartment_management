package com.jjld.domain.noise.service;

import com.jjld.domain.noise.dao.NoiseEventDAO;
import com.jjld.domain.noise.entity.Enum.ProcessStatus;
import com.jjld.domain.noise.entity.NoiseEventProcess;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class NoiseEventServiceImpl implements NoiseEventService {
    private final NoiseEventDAO noiseEventDAO;

    // 즉시처리필요/승인대기 이벤트 목록 조회
    @Override
    public Page<NoiseEventProcess> findUrgentNoiseByStatus(Pageable pageable) {
        return noiseEventDAO.findUrgentNoiseEvent(pageable);
    }
    // 상태별 소음 이벤트 목록 조회
    @Override
    public Page<NoiseEventProcess> findNoiseEventByStatus(ProcessStatus status, Pageable pageable) {
        return noiseEventDAO.findNoiseEventByStatus(status, pageable);
    }
    // 소음 이벤트 상세 조회
    @Override
    public NoiseEventProcess findNoiseEventDetail(Long noiseEventId) {
        return noiseEventDAO.findNoiseEventDetail(noiseEventId);
    }
    // 소음 이벤트 승인 처리 (이벤트 상태를 APPROVED로 변경 >> 관리자 메모 저장)
    @Override
    public void approveNoiseEvent(Long noiseEventId, String adminMemo) {
        NoiseEventProcess process =
                noiseEventDAO.findNoiseEventDetail(noiseEventId);
        // 승인 상태로 변경
        process.setStatus(ProcessStatus.APPROVED);
        // 관리자 메모 저장
        process.setAdminMemo(adminMemo);
    }
    // 소음 이벤트 보류 처리 (이벤트 상태를 HOLD로 변경 >> 관리자 메모 저장)
    @Override
    public void holdNoiseEvent(Long noiseEventId, String adminMemo) {
        NoiseEventProcess process =
                noiseEventDAO.findNoiseEventDetail(noiseEventId);
        // 보류 상태로 변경
        process.setStatus(ProcessStatus.HOLD);
        // 관리자 메모 저장
        process.setAdminMemo(adminMemo);
    }
}

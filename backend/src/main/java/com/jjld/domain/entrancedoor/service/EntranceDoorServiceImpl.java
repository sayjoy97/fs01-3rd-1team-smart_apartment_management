package com.jjld.domain.entrancedoor.service;

import com.jjld.domain.admin.entity.Admin;
import com.jjld.domain.admin.repository.AdminRepository;
import com.jjld.domain.entrancedoor.dao.EntranceDoorDAO;
import com.jjld.domain.entrancedoor.dto.EntranceGateLogResponse;
import com.jjld.domain.entrancedoor.dto.EntranceGateLogSearchCond;
import com.jjld.domain.entrancedoor.dto.EntranceGateResponse;
import com.jjld.domain.entrancedoor.entity.EntranceDoor;
import com.jjld.domain.entrancedoor.entity.EntranceGateLog;
import com.jjld.domain.entrancedoor.entity.Enum.AccessType;
import com.jjld.domain.entrancedoor.repository.EntranceDoorRepository;
import com.jjld.domain.entrancedoor.repository.EntranceGateLogRepository;
import com.jjld.domain.entrancedoor.specification.EntranceGateLogSpecification;
import com.jjld.global.exception.doorgate.DoorGateNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

@Service
@RequiredArgsConstructor
public class EntranceDoorServiceImpl implements EntranceDoorService{
    private final EntranceDoorRepository doorRepository;
    private final EntranceGateLogRepository gateLogRepository;
    private final AdminRepository adminRepository;

    // 세대 동 조회
    @Override
    public List<EntranceGateResponse> findAll() {
        List<EntranceDoor> doors = doorRepository.findAll();

        return doors.stream()
                .map(door -> {
                    // 최근 출입시간 조회
                    LocalDateTime lastAccess = gateLogRepository.findTopByHouse_HouseDongAndOutcomeTrueOrderByAccessedAtDesc(door.getHouseDong())
                            .map(EntranceGateLog::getAccessedAt)
                            .orElse(null);

                    return new EntranceGateResponse(
                            door.getHouseDong(),
                            door.getStatus().name(),
                            lastAccess
                    );
                })
                .toList();
    }

    // 공동현관 출입 로그 페이징 조회
    @Override
    public Page<EntranceGateLogResponse> search(EntranceGateLogSearchCond cond, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("accessedAt").descending());

        Specification<EntranceGateLog> spec = Specification.allOf(
                EntranceGateLogSpecification.equalHouseDong(cond.getHouseDong()),
                EntranceGateLogSpecification.equalAccessType(cond.getAccessType())
        );

        Page<EntranceGateLog> gateLogPage = gateLogRepository.findAll(spec, pageable);
        if(gateLogPage == null){
            throw new DoorGateNotFoundException("해당 페이지를 찾을 수 없습니다.");
        }

        return gateLogPage.map(log -> {
            String adminName = null;

            // 관리자를 호출한 경우만 adminName 조회
            if(log.getAccessType() == AccessType.ADMIN_CALL && log.getAdmin() != null){
                Admin admin = adminRepository.findByAdminLoginId(String.valueOf(log.getAccessLogId())).orElse(null);
                if(admin != null){
                    adminName = admin.getAdminName();
                }
            } return new EntranceGateLogResponse(log, adminName);
        });
    }
}

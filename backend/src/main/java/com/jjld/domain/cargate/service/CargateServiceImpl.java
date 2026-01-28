package com.jjld.domain.cargate.service;

import com.jjld.domain.cargate.dao.CargateDAO;
import com.jjld.domain.cargate.dto.EntryExitRecordResponse;
import com.jjld.domain.cargate.dto.ParkingSessionResponse;
import com.jjld.domain.cargate.dto.RecordDetailResponse;
import com.jjld.domain.cargate.entity.CargateEventLog;
import com.jjld.domain.cargate.entity.Vehicle;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Builder
public class CargateServiceImpl implements CargateService {
    private final CargateDAO cargateDAO;

    private final ModelMapper modelMapper;

    // 최근 7일 차량 출입현황 리스트 조회


    // 페이지&개수만큼의 리스트 호출
    @Override
    public Page<EntryExitRecordResponse> getRecordList(int size, int page) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("eventAt").descending());
        return cargateDAO.findAllCargateEventLogs(pageable).map(
                entity -> EntryExitRecordResponse.builder()
                        .cargateEventId(entity.getCargateEventId())
                        .plateNumber(entity.getVehicle().getPlateNumber())
                        .parkingStatus(entity.getGateType().name())
                        .vehicleType(entity.getVehicle().getVehicleType())
                        .eventAt(entity.getEventAt())
                        .build()
        );
    }

    // 로그아이디 별 상세조회
    @Override
    public RecordDetailResponse getDetailInfo(Long cargate_event_log_id) {
        CargateEventLog cargateLogById = cargateDAO.findCargateLogById(cargate_event_log_id);

        Vehicle vehicle = cargateLogById.getVehicle();

        List<ParkingSessionResponse> parkingSessionResponses = vehicle.getParkingSessions().stream()
                .map(parkingSession -> new ParkingSessionResponse(
                        parkingSession.getEntryAt(),
                        parkingSession.getExitAt(),
                        parkingSession.getStatus()
                ))
                .toList();

        return RecordDetailResponse.builder()
                .cargateEventId(cargateLogById.getCargateEventId())
                .plateNumber(cargateLogById.getVehicle().getPlateNumber())
                .parkingStatus(cargateLogById.getGateType().name())
                .parkingSessions(parkingSessionResponses)
                .build();
    }
}
